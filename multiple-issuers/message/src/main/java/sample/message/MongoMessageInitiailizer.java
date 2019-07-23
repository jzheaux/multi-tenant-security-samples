package sample.message;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

/**
 * @author Rob Winch
 */
@Component
class MongoMessageInitiailizer implements SmartInitializingSingleton {
	private final MessageRepository messages;

	MongoMessageInitiailizer(MessageRepository messages) {
		this.messages = messages;
	}

	@Override
	public void afterSingletonsInstantiated() {
		String robId = "1";
		String joeId = "2";

		this.messages.save(new Message(1L, robId, joeId, "Hello World"));
		this.messages.save(new Message(2L, robId, joeId,"Greetings Spring Enthusiasts"));
		this.messages.save(new Message(3L, robId, joeId,"Hola"));
		this.messages.save(new Message(4L, robId, joeId,"Hey Java Devs"));
		this.messages.save(new Message(5L, robId, joeId,"Aloha"));

		this.messages.save(new Message(100L, joeId, robId,"Hey Joe"));
	}
}
