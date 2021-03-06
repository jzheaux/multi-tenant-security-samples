package sample.tenants.message;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageApplicationTests {
	@Autowired
	private MessageRepository messages;

	@Test
	public void inbox() {
		Iterable<Message> inbox = this.messages.findByTo("1");
		Assertions.assertThat(inbox).hasSize(0);
	}

}
