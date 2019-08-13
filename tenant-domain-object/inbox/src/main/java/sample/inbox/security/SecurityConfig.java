package sample.inbox.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import sample.multitenancy.Tenant;
import sample.multitenancy.TenantHolder;
import sample.multitenancy.TenantRepository;
import sample.multitenancy.web.SubdomainTenantIdentifierResolver;
import sample.multitenancy.web.TenantResolverFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	TenantRepository tenantRepository;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		TenantResolverFilter filter = new TenantResolverFilter(this.tenantRepository);
		filter.setTenantIdentifierConverter(new SubdomainTenantIdentifierResolver());

		Map<Tenant, AuthenticationEntryPoint> entryPoints = new HashMap<>();
		AuthenticationEntryPoint authenticationEntryPoint = (request, response, e) ->
			Optional.ofNullable(TenantHolder.getTenant())
					.map(tenant -> entryPoints.computeIfAbsent(tenant, this::fromTenant))
					.orElse(new LoginUrlAuthenticationEntryPoint("/login"));

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
				.authenticationEntryPoint(authenticationEntryPoint)
				.and()
			.addFilterBefore(filter, BasicAuthenticationFilter.class);
		// @formatter:on
	}

	private AuthenticationEntryPoint fromTenant(Tenant tenant) {
		return new LoginUrlAuthenticationEntryPoint(DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/" + tenant.getName());
	}
}
