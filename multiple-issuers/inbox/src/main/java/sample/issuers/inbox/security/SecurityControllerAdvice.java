package sample.issuers.inbox.security;

import sample.issuers.inbox.user.User;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * @author Rob Winch
 */
@ControllerAdvice
public class SecurityControllerAdvice {

	@ModelAttribute("currentUser")
	User currentUser(@AuthenticationPrincipal User currentUser) {
		return currentUser;
	}
}
