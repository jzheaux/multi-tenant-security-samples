package sample.issuers.inbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class InboxApplication {

	@Bean
	WebClient webClient(ClientRegistrationRepository clientRegistrations,
			OAuth2AuthorizedClientRepository authorizedClients) {
		ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
				new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations, authorizedClients);
		oauth2.setDefaultOAuth2AuthorizedClient(true);
		return WebClient.builder()
			.apply(oauth2.oauth2Configuration())
			.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(InboxApplication.class, args);
	}
}
