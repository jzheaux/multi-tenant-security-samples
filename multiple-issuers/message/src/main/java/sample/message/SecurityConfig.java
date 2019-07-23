package sample.message;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import static com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector.fromJWKSetURL;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			.authorizeRequests()
				.anyRequest().authenticated()
				.and()
			.oauth2ResourceServer()
				.jwt();
		// @formatter:on
	}

	@Autowired
	IssuerRepository issuers;

	@Bean
	JwtDecoder jwtDecoder() {
		Map<String, JWSKeySelector<SecurityContext>> keySelectors = new HashMap<>();
		DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
		JWTClaimsSetAwareJWSKeySelector<SecurityContext> keySelector = (header, claims, context) ->
				keySelectors.computeIfAbsent(claims.getIssuer(), this::fromIssuerUri)
						.selectJWSKeys(header, context);
		jwtProcessor.setJWTClaimsSetAwareJWSKeySelector(keySelector);
		return new NimbusJwtDecoder(jwtProcessor);
	}

	JWSKeySelector<SecurityContext> fromIssuerUri(String uri) {
		return this.issuers.findByUri(uri)
				.map(this::fromIssuer)
				.orElseThrow(() -> new IllegalArgumentException("Unknown issuer " + uri));
	}

	JWSKeySelector<SecurityContext> fromIssuer(Issuer issuer) {
		try {
			return fromJWKSetURL(new URL(issuer.getJwkSetUri()));
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
}
