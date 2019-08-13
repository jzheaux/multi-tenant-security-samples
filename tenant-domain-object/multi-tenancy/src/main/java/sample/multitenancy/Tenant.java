package sample.multitenancy;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;

/**
 * @author Josh Cummings
 */
public class Tenant implements Principal {
	public static final Tenant DEFAULT_TENANT = new Tenant("public");

	private Object principal;

	private Object credentials;

	private Map<String, Object> attributes;

	public Tenant(Object principal) {
		this.principal = principal;
	}

	public Tenant(Object principal, Object credentials, Map<String, Object> attributes) {
		this.principal = principal;
		this.credentials = credentials;
		this.attributes = attributes;
	}

	public String getName() {
		return this.principal.toString();
	}

	public Object getPrincipal() {
		return this.principal;
	}

	public void setPrincipal(Object principal) {
		this.principal = principal;
	}

	public Object getCredentials() {
		return this.credentials;
	}

	public void setCredentials(Object credentials) {
		this.credentials = credentials;
	}

	public <A> A getAttribute(String attribute) {
		return (A) this.attributes.get(attribute);
	}

	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return this.principal.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tenant tenantDto = (Tenant) o;
		return Objects.equals(this.principal, tenantDto.principal);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.principal);
	}
}
