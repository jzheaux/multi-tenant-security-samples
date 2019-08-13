package sample.multitenancy.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.convert.converter.Converter;

public class HeaderTenantIdentifierResolver implements Converter<HttpServletRequest, String> {
	private final String headerName;

	public HeaderTenantIdentifierResolver() {
		this("X-Tenant-Alias");
	}

	public HeaderTenantIdentifierResolver(String headerName) {
		this.headerName = headerName;
	}

	@Override
	public String convert(HttpServletRequest request) {
		return request.getHeader(this.headerName);
	}
}
