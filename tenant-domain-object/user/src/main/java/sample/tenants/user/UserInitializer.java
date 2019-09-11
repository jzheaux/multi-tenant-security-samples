package sample.tenants.user;

import javax.transaction.Transactional;

import sample.multitenancy.TenantHolder;
import sample.multitenancy.Tenant;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

/**
 * @author Rob Winch
 */
@Component
class UserInitializer implements SmartInitializingSingleton {
	private final UserRepository users;

	UserInitializer(UserRepository users) {
		this.users = users;
	}

	@Override
	@Transactional
	public void afterSingletonsInstantiated() {
		// sha256 w/ salt encoded "password"
		String password = "73ac8218b92f7494366bf3a03c0c2ee2095d0c03a29cb34c95da327c7aa17173248af74d46ba2d4c";

		TenantHolder.setTenant(new Tenant("widgets"));
		this.users.save(new User(1L, "rob@example.com", password, "Rob", "Winch", "rob", "widgets"));
		this.users.save(new User(2L, "joe@example.com", password, "Joe", "Grandja", "joe", "widgets"));

		TenantHolder.setTenant(new Tenant("socks"));
		this.users.save(new User(3L, "josh@example.com", password, "Josh", "Cummings", "josh", "socks"));

		TenantHolder.setTenant(new Tenant("toasters"));
		this.users.save(new User(4L, "filip@example.com", password, "Filip", "Hanik", "filip", "toasters"));
		this.users.save(new User(5L, "ria@example.com", password, "Ria", "Stein", "ria", "toasters"));

		TenantHolder.clearTenant();
	}
}
