package sample.multitenancy.web;

import java.util.List;
import java.util.Map;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sample.multitenancy.Tenant;
import sample.multitenancy.TenantResolver;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientTenants implements TenantResolver {
	private final WebClient webClient = WebClient.create();

	private final String tenantsUrl;

	public WebClientTenants(String tenantsUrl) {
		this.tenantsUrl = tenantsUrl;
	}

	@Cacheable(cacheNames = "tenants")
	public Tenant resolve(String alias) {
		return findByAlias(alias).block();
	}

	private Mono<Tenant> findByAlias(String alias) {
		return this.webClient.get()
				.uri(this.tenantsUrl + "?alias={alias}", alias)
				.retrieve()
				.bodyToMono(Map.class)
				.map(this::fromMap);
	}

	private Tenant fromMap(Map<String, Object> map) {
		String alias = (String) map.get("alias");
		String password = (String) map.get("password");
		return new Tenant(alias, password, map);
	}
}
