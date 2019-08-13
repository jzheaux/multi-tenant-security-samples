package sample.multitenancy.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.convert.converter.Converter;

public class SubdomainTenantIdentifierResolver implements Converter<HttpServletRequest, String> {
	@Override
	public String convert(HttpServletRequest request) {
		String serverName = request.getServerName();
		if (serverName == null) {
			return null;
		}
		String[] segments = serverName.split("\\.");
		if (segments.length == 0) {
			return null;
		}
		return segments[0];
	}
}
