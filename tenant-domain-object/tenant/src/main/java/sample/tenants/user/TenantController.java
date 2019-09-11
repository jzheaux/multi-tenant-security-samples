package sample.tenants.user;

import java.security.SecureRandom;
import java.util.Optional;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Josh Cummings
 */
@RestController
@RequestMapping(path="/tenants", produces = MediaType.APPLICATION_JSON_VALUE)
public class TenantController {
	private static final SecureRandom random = new SecureRandom();

	private final TenantRepository tenants;

	public TenantController(TenantRepository users) {
		this.tenants = users;
	}

	@GetMapping
	Iterable<Tenant> tenants() {
		return this.tenants.findAll();
	}

	@GetMapping("/{id}")
	Optional<Tenant> findById(@PathVariable Long id) {
		return this.tenants.findById(id);
	}

	@GetMapping(params = "alias")
	Tenant findByAlias(@RequestParam String alias) {
		return this.tenants.findByAlias(alias);
	}

	@PostMapping
	Tenant add(@Valid @RequestBody Tenant tenant) {
		if (tenant.getId() == null) {
			tenant.setId(this.random.nextLong());
		}
		return this.tenants.save(tenant);
	}

	@PutMapping
	Tenant update(@Valid @RequestBody Tenant tenant) {
		if (tenant.getId() == null) {
			throw new IllegalArgumentException("tenant does not exist, must POST first");
		}
		return this.tenants.save(tenant);
	}

	@DeleteMapping("/{id}")
	Tenant delete(@PathVariable Long id) {
		return this.delete(id);
	}
}
