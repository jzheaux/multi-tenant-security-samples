package sample.issuers.webflux.message;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSecurityContextJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWKSecurityContext;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SecurityConfig {
	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		// @formatter:off
		http
			.authorizeExchange()
				.anyExchange().authenticated()
				.and()
			.oauth2ResourceServer()
				.jwt();

		return http.build();
		// @formatter:on
	}

	@Bean
	ReactiveJwtDecoder jwtDecoder() {
		JWKSecurityContextJWKSet jwkSource = new JWKSecurityContextJWKSet();
		JWSKeySelector<JWKSecurityContext> jwsKeySelector = new JWSAlgorithmFamilyJWSKeySelector<>(JWSAlgorithm.Family.RSA, jwkSource);
		DefaultJWTProcessor<JWKSecurityContext> delegate = new DefaultJWTProcessor<>();
		delegate.setJWSKeySelector(jwsKeySelector);
		Converter<String, Converter<SignedJWT, Flux<JWK>>> selectorConverter = selectorConverter();
		Map<String, Converter<SignedJWT, Flux<JWK>>> selectors = new ConcurrentHashMap<>();
		Converter<JWT, Mono<JWTClaimsSet>> jwtProcessor = jwt ->
			selectors.computeIfAbsent(claimsSet((SignedJWT) jwt).getIssuer(), selectorConverter::convert)
				.convert((SignedJWT) jwt)
				.collectList()
				.map((jwks) -> createClaimsSet(delegate, jwt, new JWKSecurityContext(jwks)));
		return new NimbusReactiveJwtDecoder(jwtProcessor);
	}

	Converter<String, Converter<SignedJWT, Flux<JWK>>> selectorConverter() {
		Map<String, String> jwkSetUris = new HashMap<>();
		jwkSetUris.put("http://idp:9999/auth/realms/tenantone", "http://idp:9999/auth/realms/tenantone/protocol/openid-connect/certs");
		jwkSetUris.put("http://idp:9999/auth/realms/tenanttwo", "http://idp:9999/auth/realms/tenanttwo/protocol/openid-connect/certs");

		return issuer -> Optional.ofNullable(jwkSetUris.get(issuer))
				.map(this::fromJwkSetUri)
				.orElseThrow(() -> new IllegalArgumentException("Unknown issuer " + issuer));
	}

	Converter<SignedJWT, Flux<JWK>> fromJwkSetUri(String uri) {
		try {
			ReactiveRemoteJWKSource source = new ReactiveRemoteJWKSource(uri);
			return (signedJWT) -> {
				JWKSelector selector = this.createSelector(signedJWT.getHeader());
				return source.get(selector).onErrorMap((e) ->
					new IllegalStateException("Could not obtain the keys", e));
			};
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private JWKSelector createSelector(JWSHeader header) {
		if (!JWSAlgorithm.Family.RSA.contains(header.getAlgorithm())) {
			throw new JwtException("Unsupported algorithm of " + header.getAlgorithm());
		} else {
			return new JWKSelector(JWKMatcher.forJWSHeader(header));
		}
	}

	private JWTClaimsSet claimsSet(SignedJWT jwt) {
		try {
			return jwt.getJWTClaimsSet();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static <C extends SecurityContext> JWTClaimsSet createClaimsSet(JWTProcessor<C> jwtProcessor, JWT parsedToken, C context) {
		try {
			return jwtProcessor.process(parsedToken, context);
		} catch (JOSEException | BadJOSEException var4) {
			throw new JwtException("Failed to validate the token", var4);
		}
	}

	static class ReactiveRemoteJWKSource {
		private final AtomicReference<Mono<JWKSet>> cachedJWKSet = new AtomicReference(Mono.empty());
		private WebClient webClient = WebClient.create();
		private final String jwkSetURL;

		ReactiveRemoteJWKSource(String jwkSetURL) {
			Assert.hasText(jwkSetURL, "jwkSetURL cannot be empty");
			this.jwkSetURL = jwkSetURL;
		}

		public Flux<JWK> get(JWKSelector jwkSelector) {
			return this.cachedJWKSet.get()
					.switchIfEmpty(Mono.defer(this::getJWKSet))
					.flatMap((jwkSet) -> this.get(jwkSelector, jwkSet))
					.switchIfEmpty(Mono.defer(() ->
							this.getJWKSet().map(jwkSelector::select)))
					.flatMapMany(Flux::fromIterable);
		}

		private Mono<List<JWK>> get(JWKSelector jwkSelector, JWKSet jwkSet) {
			return Mono.defer(() -> {
				List<JWK> matches = jwkSelector.select(jwkSet);
				if (!matches.isEmpty()) {
					return Mono.just(matches);
				} else {
					String soughtKeyID = getFirstSpecifiedKeyID(jwkSelector.getMatcher());
					if (soughtKeyID == null) {
						return Mono.just(Collections.emptyList());
					} else {
						return jwkSet.getKeyByKeyId(soughtKeyID) != null ? Mono.just(Collections.emptyList()) : Mono.empty();
					}
				}
			});
		}

		private Mono<JWKSet> getJWKSet() {
			return this.webClient.get().uri(this.jwkSetURL, new Object[0]).retrieve().bodyToMono(String.class).map(this::parse).doOnNext((jwkSet) -> {
				this.cachedJWKSet.set(Mono.just(jwkSet));
			}).cache();
		}

		private JWKSet parse(String body) {
			try {
				return JWKSet.parse(body);
			} catch (ParseException var3) {
				throw new RuntimeException(var3);
			}
		}

		protected static String getFirstSpecifiedKeyID(JWKMatcher jwkMatcher) {
			Set<String> keyIDs = jwkMatcher.getKeyIDs();
			if (keyIDs != null && !keyIDs.isEmpty()) {
				Iterator var2 = keyIDs.iterator();

				String id;
				do {
					if (!var2.hasNext()) {
						return null;
					}

					id = (String)var2.next();
				} while(id == null);

				return id;
			} else {
				return null;
			}
		}
	}

}
