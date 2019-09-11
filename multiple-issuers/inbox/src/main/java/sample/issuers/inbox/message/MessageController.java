package sample.issuers.inbox.message;

import java.util.Collections;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Rob Winch
 */
@Controller
@RequestMapping("/messages")
public class MessageController {
	private final MessageService messages;

	public MessageController(MessageService messages) {
		this.messages = messages;
	}

	@GetMapping("/inbox")
	ModelAndView inbox() {
		return new ModelAndView("messages/inbox",
				Collections.singletonMap("messages", this.messages.inbox().collectList().block()));
	}

	@GetMapping("/{id}")
	ModelAndView message(@PathVariable String id) {
		return new ModelAndView("messages/view",
				Collections.singletonMap("message", this.messages.findById(id).block()));
	}

	@DeleteMapping("/{id}")
	String deleteById(@PathVariable String id) {
		this.messages.deleteById(id).block();
		return "redirect:/messages/inbox?deleted";
	}
}
