package sample.multitenancy.web;

import java.util.Map;

import sample.multitenancy.Tenant;
import sample.multitenancy.TenantRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientTenantRepository implements TenantRepository {
	private final WebClient webClient = WebClient.create();

	private final String tenantsUrl;

	public WebClientTenantRepository(String tenantsUrl) {
		this.tenantsUrl = tenantsUrl;
	}

	@Cacheable(cacheNames = "tenants")
	public Tenant findByAlias(String alias) {
		return this.webClient.get()
				.uri(this.tenantsUrl + "?alias={alias}", alias)
				.retrieve()
				.bodyToMono(Map.class)
				.map(this::fromMap)
				.block();
	}

	private Tenant fromMap(Map<String, Object> map) {
		String alias = (String) map.get("alias");
		String password = (String) map.get("password");
		return new Tenant(alias, password, map);
	}
}
