package sample.multitenancy.discriminator;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Filter;
import org.hibernate.Session;
import sample.multitenancy.TenantHolder;

@Aspect
public class TenantAspect {
	@PersistenceContext
	EntityManager entityManager;

	@Before("execution(* org.springframework.data.repository.Repository+.*(..))")
	public void addTenantFilter() {
		Optional.ofNullable(TenantHolder.getTenant()).ifPresent(
			tenant -> {
				Session session = this.entityManager.unwrap(Session.class);
				Filter filter = session.enableFilter("tenantFilter");
				filter.setParameter("tenantId", tenant.getName());
			}
		);
	}
}
