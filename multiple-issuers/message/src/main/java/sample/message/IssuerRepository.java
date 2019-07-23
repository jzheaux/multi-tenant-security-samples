package sample.message;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Josh Cummings
 */
public interface IssuerRepository extends CrudRepository<Issuer, Long> {
	Optional<Issuer> findByUri(String uri);
}
