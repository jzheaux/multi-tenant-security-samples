package sample.user;

import sample.multitenancy.TenantRepository;
import sample.multitenancy.discriminator.TenantAspect;
import sample.multitenancy.web.WebClientTenantRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserApplication {

	@Value("${tenants-url}") String tenantsUrl;

	@Bean
	TenantRepository tenantRepository() {
		return new WebClientTenantRepository(this.tenantsUrl);
	}

	@Bean
	TenantAspect tenantAspect() {
		return new TenantAspect();
	}

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}
}
