package sample.message;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

/**
 * @author Josh Cummings
 */
@Component
class MongoIssuerInitializer implements SmartInitializingSingleton {
	private final IssuerRepository issuers;

	MongoIssuerInitializer(IssuerRepository issuers) {
		this.issuers = issuers;
	}

	@Override
	public void afterSingletonsInstantiated() {
		this.issuers.save(new Issuer(1L,
				"http://idp:9999/auth/realms/tenantone",
				"http://idp:9999/auth/realms/tenantone/protocol/openid-connect/certs"));
		this.issuers.save(new Issuer(2L,
				"http://idp:9999/auth/realms/tenanttwo",
				"http://idp:9999/auth/realms/tenanttwo/protocol/openid-connect/certs"));
	}
}
