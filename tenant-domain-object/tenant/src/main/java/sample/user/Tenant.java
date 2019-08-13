package sample.user;

import java.util.Objects;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Josh Cummings
 */
@Document
public class Tenant {
	@Id
	private Long id;

	@NotEmpty(message = "This field is required")
	private String name;

	@NotEmpty(message = "This field is required")
	private String alias;

	@NotEmpty
	private String password;

	@NotEmpty(message = "This field is required")
	private String issuerUri;

	@NotEmpty(message = "This field is required")
	private String tokenVerificationMode;

	public Tenant() {}

	public Tenant(Tenant tenant) {
		this(tenant.getId(), tenant.getName(), tenant.getAlias(),
				tenant.getPassword(), tenant.getIssuerUri(), tenant.getTokenVerificationMode());
	}

	public Tenant(Long id, String name, String alias, String password, String issuerUri,
			String tokenVerificationMode) {

		this.id = id;
		this.name = name;
		this.alias = alias;
		this.password = password;
		this.issuerUri = issuerUri;
		this.tokenVerificationMode = tokenVerificationMode;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIssuerUri() {
		return issuerUri;
	}

	public void setIssuerUri(String issuerUri) {
		this.issuerUri = issuerUri;
	}

	public String getTokenVerificationMode() {
		return tokenVerificationMode;
	}

	public void setTokenVerificationMode(String tokenVerificationMode) {
		this.tokenVerificationMode = tokenVerificationMode;
	}

	@Override
	public String toString() {
		return this.alias;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tenant tenant = (Tenant) o;
		return this.alias.equals(tenant.alias);
	}

	@Override
	public int hashCode() {
		return Objects.hash(alias);
	}
}
