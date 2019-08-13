package sample.inbox.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.TenantAuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
import static org.springframework.security.web.TenantAuthenticationEntryPoint.resolveFromSubdomain;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	ClientRegistrationRepository repository;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		Map<String, AuthenticationEntryPoint> entryPoints = new HashMap<>();
		AuthenticationEntryPoint authenticationEntryPoint = resolveFromSubdomain(
			tenant -> Optional.ofNullable(repository.findByRegistrationId(tenant))
					.map(clientRegistration -> entryPoints.computeIfAbsent(
							clientRegistration.getRegistrationId(),
							registrationId -> new LoginUrlAuthenticationEntryPoint(
									DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/" + registrationId)))
					.orElse(new LoginUrlAuthenticationEntryPoint("/login")));

		// @formatter:off
		http
			.authorizeRequests()
				.antMatchers("/login", "/webjars/**").permitAll()
				.anyRequest().authenticated()
				.and()
			.oauth2Login()
				.and()
			.oauth2Client()
				.and()
			.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint);
		// @formatter:on
	}
}
