package sample.issuers.message;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageApplicationTests {
	@Autowired
	private MessageRepository messages;

	@Test
	public void inbox() {
		Iterable<Message> inbox = this.messages.findByTo("1");
		assertThat(inbox).hasSize(5);
	}

}
