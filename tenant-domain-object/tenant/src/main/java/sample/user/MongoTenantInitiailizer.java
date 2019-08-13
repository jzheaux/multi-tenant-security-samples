package sample.user;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

/**
 * @author Josh Cummings
 */
@Component
class MongoTenantInitiailizer implements SmartInitializingSingleton {
	private final TenantRepository tenants;

	MongoTenantInitiailizer(TenantRepository users) {
		this.tenants = users;
	}

	@Override
	public void afterSingletonsInstantiated() {
 		this.tenants.save(new Tenant(1L, "Wild Widgets", "widgets", "widgets", "http://idp:9999/auth/realms/widgets", "jwt"));
		this.tenants.save(new Tenant(2L, "Tasty Toasters", "toasters", "toasters", "http://idp:9999/auth/realms/toasters", "jwt"));
		this.tenants.save(new Tenant(3L, "Socks A Sizzlin'", "socks", "socks", "http://idp:9999/auth/realms/socks", "opaqueToken"));
	}
}
