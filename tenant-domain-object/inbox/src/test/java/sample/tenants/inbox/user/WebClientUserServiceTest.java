package sample.tenants.inbox.user;

import org.junit.Test;

import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Rob Winch
 */
public class WebClientUserServiceTest {
	WebClientUserService users = new WebClientUserService(WebClient.create(), "http://localhost:8081/users");

	@Test
	public void save() {
		/*User user = new User(null, "new@example.com", "password", "New", "User");
		this.users.save(user).block();*/
	}

	@Test
	public void findByEmail() {
		/*User rob = this.users.findByEmail("rob@example.com").block();

		assertThat(rob).isNotNull();*/
	}
}