package sample.multitenancy.web;

import reactor.core.publisher.Mono;
import sample.multitenancy.TenantHolder;

import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

public class TenantHeaderExchangeFilterFunction implements ExchangeFilterFunction {
	private final String headerName;

	public TenantHeaderExchangeFilterFunction() {
		this("X-Tenant-Alias");
	}

	public TenantHeaderExchangeFilterFunction(String headerName) {
		this.headerName = headerName;
	}

	@Override
	public Mono<ClientResponse> filter(ClientRequest clientRequest, ExchangeFunction exchangeFunction) {
		clientRequest.headers().set(this.headerName, TenantHolder.getTenant().getName());
		return exchangeFunction.exchange(clientRequest);
	}
}
