package sample.multitenancy.oauth2;

import sample.multitenancy.Tenant;
import sample.multitenancy.TenantRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;

public class TenantClientRegistrationRepository implements ClientRegistrationRepository {
	private final TenantRepository tenantRepository;

	public TenantClientRegistrationRepository(TenantRepository tenantRepository) {
		this.tenantRepository = tenantRepository;
	}

	@Override
	@Cacheable(cacheNames = "client-registrations")
	public ClientRegistration findByRegistrationId(String registrationId) {
		Tenant tenant = this.tenantRepository.findByAlias(registrationId);
		return ClientRegistrations.fromIssuerLocation(tenant.getAttribute("issuerUri")).build();
	}
}
