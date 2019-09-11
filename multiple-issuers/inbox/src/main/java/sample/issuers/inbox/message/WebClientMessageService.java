package sample.issuers.inbox.message;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sample.issuers.inbox.user.User;
import sample.issuers.inbox.user.UserService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Rob Winch
 */
@Component
public class WebClientMessageService implements MessageService {
	private final WebClient webClient;

	private final String messagesUrl;

	private final UserService users;

	public WebClientMessageService(WebClient webClient,
			@Value("${messages-url}") String messagesUrl, UserService users) {
		this.webClient = webClient;
		this.messagesUrl = messagesUrl;
		this.users = users;
	}

	@Override
	public Flux<Message> inbox() {
		return this.webClient.get()
			.uri(this.messagesUrl + "/inbox")
			.retrieve()
			.bodyToFlux(MessageDto.class)
			.flatMap(this::toMessage);
	}

	@Override
	public Mono<Message> findById(String id) {
		return this.webClient.get()
				.uri(this.messagesUrl + "/{id}", id)
				.retrieve()
				.bodyToMono(MessageDto.class)
				.flatMap(this::toMessage);
	}

	@Override
	public Mono<Void> deleteById(String id) {
		return this.webClient.delete()
			.uri(this.messagesUrl + "/{id}", id)
			.exchange()
			.then(Mono.empty());
	}

	private Mono<Message> toMessage(MessageDto dto) {
		Mono<User> to = this.users.findById(dto.getTo());
		Mono<User> from = this.users.findById(dto.getFrom());
		return Mono.zip(to, from)
				.map(tuple -> new Message(dto.getId(), tuple.getT1(), tuple.getT2(), dto.getText()));
	}
}
