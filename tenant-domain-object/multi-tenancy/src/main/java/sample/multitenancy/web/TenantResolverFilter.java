package sample.multitenancy.web;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sample.multitenancy.Tenant;
import sample.multitenancy.TenantHolder;
import sample.multitenancy.TenantResolver;

import org.springframework.web.filter.OncePerRequestFilter;

public class TenantResolverFilter extends OncePerRequestFilter {
	final TenantResolver tenantResolver;

	public TenantResolverFilter(TenantResolver tenantResolver) {
		this.tenantResolver = tenantResolver;
	}

	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String alias = request.getHeader("X-Tenant-Alias");

		try {
			Tenant tenant = this.tenantResolver.resolve(alias);
			TenantHolder.setTenant(tenant);
			filterChain.doFilter(request, response);
		} finally {
			TenantHolder.clearTenant();
		}
	}
}
