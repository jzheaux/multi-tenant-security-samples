package sample.multitenancy;

public class TenantHolder {
	private static final ThreadLocal<Tenant> context = new ThreadLocal<>();

	public static Tenant getTenant() {
		return context.get();
	}

	public static void setTenant(Tenant tenant) {
		context.set(tenant);
	}

	public static void clearTenant() {
		context.remove();
	}
}
