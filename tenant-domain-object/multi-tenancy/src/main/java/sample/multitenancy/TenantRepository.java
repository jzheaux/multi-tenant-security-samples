package sample.multitenancy;

public interface TenantRepository {
	Tenant findByAlias(String alias);
}
