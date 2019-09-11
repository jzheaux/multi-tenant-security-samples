package sample.issuers.inbox.user;

import javax.validation.Valid;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Rob Winch
 */
@Controller
@RequestMapping("/users/")
public class UserController {
	private final UserService users;

	public UserController(UserService users) {
		this.users = users;
	}

	@GetMapping("/signup")
	String signupForm(@ModelAttribute User user) {
		return "users/form";
	}

	@PostMapping("/signup")
	String signup(@Valid User user, BindingResult result) {
		if(result.hasErrors()) {
			return signupForm(user);
		}
		return Mono.just(user)
				.flatMap(u -> this.users.save(u))
				.then(Mono.just("redirect:/")).block();
	}
}
