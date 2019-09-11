package sample.issuers.inbox.security;

import java.util.Collection;
import java.util.Map;

import sample.issuers.inbox.user.User;
import sample.issuers.inbox.user.UserService;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

/**
 * @author Rob Winch
 */
@Component
public class UserServiceOAuth2UserService
		implements OAuth2UserService<OidcUserRequest, OidcUser> {

	private final OidcUserService delegate = new OidcUserService();

	private final UserService users;

	public UserServiceOAuth2UserService(UserService users) {
		this.users = users;
	}

	@Override
	public OidcUser loadUser(OidcUserRequest oidcUserRequest)
			throws OAuth2AuthenticationException {
		return create(this.delegate.loadUser(oidcUserRequest));
	}

	private OidcUser create(OidcUser oidcUser) {
		return this.users.findByEmail(oidcUser.getEmail())
				.map(u -> new CustomOidcUser(u, oidcUser))
				.block();
	}

	private class CustomOidcUser extends User implements OidcUser {
		private OidcUser oidcUser;

		public CustomOidcUser(User u, OidcUser oidcUser) {
			super(u);
			this.oidcUser = oidcUser;
		}

		public Map<String, Object> getClaims() {
			return oidcUser.getClaims();
		}

		public OidcUserInfo getUserInfo() {
			return oidcUser.getUserInfo();
		}

		public OidcIdToken getIdToken() {
			return oidcUser.getIdToken();
		}

		public Collection<? extends GrantedAuthority> getAuthorities() {
			return oidcUser.getAuthorities();
		}

		public Map<String, Object> getAttributes() {
			return oidcUser.getAttributes();
		}

		public String getName() {
			return oidcUser.getName();
		}
	}
}
