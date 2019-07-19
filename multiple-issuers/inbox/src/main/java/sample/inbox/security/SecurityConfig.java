package sample.inbox.security;

import java.util.HashMap;
import java.util.Map;

import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;

import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;

@EnableWebFluxSecurity
public class SecurityConfig {
	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
			ReactiveClientRegistrationRepository repository) {
		Map<String, ServerAuthenticationEntryPoint> entryPoints = new HashMap<>();
		// @formatter:off
		http
				.authorizeExchange()
					.pathMatchers("/login", "/webjars/**").permitAll()
					.anyExchange().authenticated()
					.and()
				.oauth2Login()
					.and()
				.oauth2Client()
					.and()
				.exceptionHandling()
					.authenticationEntryPoint((exchange, exception) -> {
						String host = exchange.getRequest().getHeaders().getFirst("Host");
						String tenant = host.split("\\.")[0];
						return repository.findByRegistrationId(tenant)
								.map(clientRegistration -> entryPoints.computeIfAbsent(
										clientRegistration.getRegistrationId(),
										registrationId -> new RedirectServerAuthenticationEntryPoint(
												DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/" + registrationId)))
								.switchIfEmpty(Mono.just(new RedirectServerAuthenticationEntryPoint("/login")))
								.flatMap(entryPoint -> entryPoint.commence(exchange, exception));
					});

		return http.build();
		// @formatter:on
	}
}
