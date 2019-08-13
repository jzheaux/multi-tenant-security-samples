package sample.user;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Josh Cummings
 */
public interface TenantRepository extends CrudRepository<Tenant, Long> {
	Tenant findByAlias(String alias);
}
