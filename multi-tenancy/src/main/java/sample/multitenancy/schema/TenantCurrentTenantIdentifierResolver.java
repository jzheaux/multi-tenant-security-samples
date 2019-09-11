package sample.multitenancy.schema;

import java.util.Optional;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import sample.multitenancy.Tenant;
import sample.multitenancy.TenantHolder;

public class TenantCurrentTenantIdentifierResolver
		implements CurrentTenantIdentifierResolver {

	@Override
	public String resolveCurrentTenantIdentifier() {
		return Optional.ofNullable(TenantHolder.getTenant())
				.orElse(Tenant.DEFAULT_TENANT).getName();
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}
}
