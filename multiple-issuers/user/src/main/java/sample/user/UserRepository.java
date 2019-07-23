package sample.user;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Rob Winch
 */
public interface UserRepository extends CrudRepository<User, Long> {
	User findByEmail(String email);

	User findByAlias(String alias);
}
