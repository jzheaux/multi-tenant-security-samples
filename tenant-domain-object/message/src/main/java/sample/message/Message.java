package sample.message;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


/**
 * @author Rob Winch
 */
@Entity
public class Message {
	@Id
	private Long id;

	private String to;

	@Column(name="from_address")
	private String from;

	private String text;

	public Message() {}

	public Message(Long id, String to, String from, String text) {
		this.id = id;
		this.to = to;
		this.from = from;
		this.text = text;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTo() {
		return this.to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return this.from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
