package sample.tenants.message;

import sample.multitenancy.Tenant;
import sample.multitenancy.TenantHolder;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Rob Winch
 */
@Component
class MessageInitializer implements SmartInitializingSingleton {
	private final MessageRepository messages;
	private final TransactionTemplate transaction;

	MessageInitializer(MessageRepository messages, TransactionTemplate transaction) {
		this.messages = messages;
		this.transaction = transaction;
	}

	@Override
	public void afterSingletonsInstantiated() {
		String robId = "1";
		String joeId = "2";
		String joshId = "3";

		TenantHolder.setTenant(new Tenant("widgets"));
		this.transaction.execute(status -> {
			this.messages.save(new Message(1L, robId, joeId, "Hello World"));
			this.messages.save(new Message(2L, robId, joeId, "Greetings Spring Enthusiasts"));
			this.messages.save(new Message(3L, robId, joeId, "Hola"));
			this.messages.save(new Message(4L, robId, joeId, "Hey Java Devs"));
			this.messages.save(new Message(5L, robId, joeId, "Aloha"));
			this.messages.save(new Message(6L, joshId, robId, "Welcome to Spring"));
			return null;
		});

		TenantHolder.setTenant(new Tenant("widgets"));
		this.transaction.execute(status -> {
			this.messages.save(new Message(100L, joeId, robId, "Hey Joe"));
			this.messages.save(new Message(101L, joeId, joshId, "Ora Viva"));
			return null;
		});

		TenantHolder.setTenant(new Tenant("socks"));
		this.transaction.execute(status -> {
			this.messages.save(new Message(1000L, joshId, robId, "Welcome to Spring"));
			return null;
		});
	}
}
