package sample.issuers.message;

import java.util.Optional;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rob Winch
 */
@RestController
@RequestMapping("/messages")
public class MessageController {
	private final MessageRepository messages;

	public MessageController(MessageRepository messages) {
		this.messages = messages;
	}

	@GetMapping("/inbox")
	Iterable<Message> inbox(@CurrentUserId String currentUserId) {
		return this.messages.findByTo(currentUserId);
	}

	@GetMapping("/{id}")
	@PostAuthorize("returnObject?.to == authentication?.tokenAttributes['user_id']")
	Optional<Message> findById(@PathVariable Long id) {
		return this.messages.findById(id);
	}

	@DeleteMapping("/{id}")
	void deleteById(@PathVariable Long id) {
		this.messages.deleteById(id);
	}
}
