package sample.tenants.user;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import sample.multitenancy.Tenant;
import sample.multitenancy.TenantHolder;
import sample.multitenancy.TenantRepository;
import sample.multitenancy.web.TenantResolverFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;

import static org.springframework.security.oauth2.jwt.JwtDecoders.fromIssuerLocation;

@Configuration
public class UserSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	TenantRepository tenantRepository;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		TenantResolverFilter filter = new TenantResolverFilter(this.tenantRepository);
		http
			.authorizeRequests()
				.anyRequest().authenticated()
				.and()
			.oauth2ResourceServer()
				.authenticationManagerResolver(authenticationManagerResolver())
				.and()
			.addFilterBefore(filter, BearerTokenAuthenticationFilter.class);
	}


	private AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver() {
		Map<Tenant, AuthenticationManager> authenticationManagers = new HashMap<>();
		return request ->
			Optional.ofNullable(TenantHolder.getTenant())
					.map(tenant -> authenticationManagers.computeIfAbsent(tenant, this::fromTenant))
					.orElseThrow(() -> new IllegalArgumentException("Unknown tenant"));
	}

	AuthenticationManager fromTenant(Tenant tenant) {
		String tokenVerificationMode = tenant.getAttribute("tokenVerificationMode");
		if ("jwt".equals(tokenVerificationMode)) {
			JwtDecoder jwtDecoder = fromIssuerLocation(tenant.getAttribute("issuerUri"));
			return new JwtAuthenticationProvider(jwtDecoder)::authenticate;
		}
		if ("opaqueToken".equals(tokenVerificationMode)) {
			OpaqueTokenIntrospector client =
					new NimbusOpaqueTokenIntrospector(
							tenant.getAttribute("issuerUri") + "/introspect",
							tenant.getName(),
							(String) tenant.getCredentials());
			return new OpaqueTokenAuthenticationProvider(client)::authenticate;
		}
		throw new IllegalArgumentException("Unsupported token verification mode " + tokenVerificationMode);
	}
}
