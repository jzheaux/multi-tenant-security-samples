package sample.user;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rob Winch
 */
@RestController
@RequestMapping(path="/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserController {
	private final Random random = new SecureRandom();

	private final UserRepository users;

	public UserController(UserRepository users) {
		this.users = users;
	}

	@GetMapping
	Iterable<User> users() {
		return this.users.findAll();
	}

	@GetMapping("/{id}")
	Optional<User> findById(@PathVariable Long id) {
		return this.users.findById(id);
	}

	@GetMapping(params = "email")
	User findByEmail(@RequestParam String email) {
		return this.users.findByEmail(email);
	}

	@PostMapping
	User save(@Valid @RequestBody User user) {
		if (user.getId() == null) {
			user.setId(this.random.nextLong());
		}
		return this.users.save(user);
	}
}
