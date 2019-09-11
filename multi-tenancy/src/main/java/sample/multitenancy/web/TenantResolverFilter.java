package sample.multitenancy.web;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sample.multitenancy.Tenant;
import sample.multitenancy.TenantHolder;
import sample.multitenancy.TenantRepository;

import org.springframework.core.convert.converter.Converter;
import org.springframework.web.filter.OncePerRequestFilter;

public class TenantResolverFilter extends OncePerRequestFilter {
	private Converter<HttpServletRequest, String> tenantIdentifierConverter = new HeaderTenantIdentifierResolver();
	private final TenantRepository tenantRepository;

	public TenantResolverFilter(TenantRepository tenantRepository) {
		this.tenantRepository = tenantRepository;
	}

	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String alias = this.tenantIdentifierConverter.convert(request);

		try {
			Tenant tenant = this.tenantRepository.findByAlias(alias);
			TenantHolder.setTenant(tenant);
			filterChain.doFilter(request, response);
		} finally {
			TenantHolder.clearTenant();
		}
	}

	public void setTenantIdentifierConverter(Converter<HttpServletRequest, String> tenantIdentifierConverter) {
		this.tenantIdentifierConverter = tenantIdentifierConverter;
	}
}
