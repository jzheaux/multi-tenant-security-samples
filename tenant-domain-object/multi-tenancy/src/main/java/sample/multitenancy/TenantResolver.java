package sample.multitenancy;

public interface TenantResolver {
	Tenant resolve(String id);
}
