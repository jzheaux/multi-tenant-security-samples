package org.springframework.security.web;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class TenantAuthenticationEntryPoint<T> implements AuthenticationEntryPoint {
	private Converter<HttpServletRequest, AuthenticationEntryPoint> authenticationEntryPointResolver;

	public TenantAuthenticationEntryPoint(Converter<HttpServletRequest, T> tenantResolver,
			Converter<T, AuthenticationEntryPoint> authenticationEntryPointResolver) {
		Assert.notNull(tenantResolver, "tenantResolver cannot be null");
		Assert.notNull(authenticationEntryPointResolver, "authenticationEntryPointResolver cannot be null");

		this.authenticationEntryPointResolver = request -> {
			Optional<T> context = Optional.ofNullable(tenantResolver.convert(request));
			return context.map(authenticationEntryPointResolver::convert)
					.orElseThrow(() -> new IllegalArgumentException
							("Could not resolve AuthenticationEntryPoint by reference " + context.orElse(null)));
		};
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
		this.authenticationEntryPointResolver.convert(request)
				.commence(request, response, e);
	}

	/**
	 * Creates an {@link AuthenticationEntryPoint} that will use a hostname's first label as
	 * the resolution key for the underlying {@link AuthenticationEntryPoint}.
	 *
	 * For example, you might have a set of {@link AuthenticationManager}s defined like so:
	 *
	 * <pre>
	 * 	Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();
	 *  authenticationManagers.put("tenantOne", managerOne());
	 *  authenticationManagers.put("tenantTwo", managerTwo());
	 * </pre>
	 *
	 * And that your system serves hostnames like <pre>https://tenantOne.example.org</pre>.
	 *
	 * Then, you could create an {@link AuthenticationEntryPoint} that uses the "tenantOne" value from
	 * the hostname to resolve Tenant One's {@link AuthenticationManager} like so:
	 *
	 * <pre>
	 *	AuthenticationEntryPoint<HttpServletRequest> resolver =
	 *			resolveFromSubdomain(authenticationManagers::get);
	 * </pre>
	 *
	 * {@link HttpServletRequest}
	 * @param resolver A {@link String}-resolving {@link AuthenticationEntryPoint}
	 * @return A hostname-resolving {@link AuthenticationEntryPoint}
	 */
	public static AuthenticationEntryPoint
			resolveFromSubdomain(Converter<String, AuthenticationEntryPoint> resolver) {

		return new TenantAuthenticationEntryPoint<>(request ->
				Optional.ofNullable(request.getServerName())
						.map(host -> host.split("\\."))
						.filter(segments -> segments.length > 0)
						.map(segments -> segments[0]).orElse(null), resolver);
	}

	/**
	 * Creates an {@link AuthenticationEntryPoint} that will use a request path's first segment as
	 * the resolution key for the underlying {@link AuthenticationEntryPoint}.
	 *
	 * For example, you might have a set of {@link AuthenticationManager}s defined like so:
	 *
	 * <pre>
	 * 	Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();
	 *  authenticationManagers.put("tenantOne", managerOne());
	 *  authenticationManagers.put("tenantTwo", managerTwo());
	 * </pre>
	 *
	 * And that your system serves requests like <pre>https://example.org/tenantOne</pre>.
	 *
	 * Then, you could create an {@link AuthenticationEntryPoint} that uses the "tenantOne" value from
	 * the request to resolve Tenant One's {@link AuthenticationManager} like so:
	 *
	 * <pre>
	 *	AuthenticationEntryPoint<HttpServletRequest> resolver =
	 *			resolveFromPath(authenticationManagers::get);
	 * </pre>
	 *
	 * {@link HttpServletRequest}
	 * @param resolver A {@link String}-resolving {@link AuthenticationEntryPoint}
	 * @return A path-resolving {@link AuthenticationEntryPoint}
	 */
	public static AuthenticationEntryPoint
			resolveFromPath(Converter<String, AuthenticationEntryPoint> resolver) {

		return new TenantAuthenticationEntryPoint<>(request ->
				Optional.ofNullable(request.getRequestURI())
						.map(UriComponentsBuilder::fromUriString)
						.map(UriComponentsBuilder::build)
						.map(UriComponents::getPathSegments)
						.filter(segments -> !segments.isEmpty())
						.map(segments -> segments.get(0)).orElse(null), resolver);
	}

	/**
	 * Creates an {@link AuthenticationEntryPoint} that will use a request headers's value as
	 * the resolution key for the underlying {@link AuthenticationEntryPoint}.
	 *
	 * For example, you might have a set of {@link AuthenticationManager}s defined like so:
	 *
	 * <pre>
	 * 	Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();
	 *  authenticationManagers.put("tenantOne", managerOne());
	 *  authenticationManagers.put("tenantTwo", managerTwo());
	 * </pre>
	 *
	 * And that your system serves requests with a header like <pre>X-Tenant-Id: tenantOne</pre>.
	 *
	 * Then, you could create an {@link AuthenticationEntryPoint} that uses the "tenantOne" value from
	 * the request to resolve Tenant One's {@link AuthenticationManager} like so:
	 *
	 * <pre>
	 *	AuthenticationEntryPoint<HttpServletRequest> resolver =
	 *			resolveFromHeader("X-Tenant-Id", authenticationManagers::get);
	 * </pre>
	 *
	 * {@link HttpServletRequest}
	 * @param resolver A {@link String}-resolving {@link AuthenticationEntryPoint}
	 * @return A header-resolving {@link AuthenticationEntryPoint}
	 */
	public static AuthenticationEntryPoint
			resolveFromHeader(String headerName, Converter<String, AuthenticationEntryPoint> resolver) {

		return new TenantAuthenticationEntryPoint<>
				(request -> request.getHeader(headerName), resolver);
	}
}
