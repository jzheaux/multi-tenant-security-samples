package sample.issuers.message;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Josh Cummings
 */
@Document
public class Issuer {
	@Id
	private Long id;

	private String uri;

	private String jwkSetUri;

	public Issuer() {}

	public Issuer(Long id, String uri, String jwkSetUri) {
		this.id = id;
		this.uri = uri;
		this.jwkSetUri = jwkSetUri;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getJwkSetUri() {
		return jwkSetUri;
	}

	public void setJwkSetUri(String jwkSetUri) {
		this.jwkSetUri = jwkSetUri;
	}
}