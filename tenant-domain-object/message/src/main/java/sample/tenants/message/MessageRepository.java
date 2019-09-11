package sample.tenants.message;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Rob Winch
 */
public interface MessageRepository extends CrudRepository<Message, Long> {
	Iterable<Message> findByTo(String id);

	Optional<Message> findById(Long id);
}
