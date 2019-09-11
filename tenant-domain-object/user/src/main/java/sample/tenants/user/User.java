package sample.tenants.user;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;


/**
 * @author Josh Cummings
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "tenantId", type = "string")})
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class User {
	@Id
	@GeneratedValue
	private Long id;

	private String email;

	private String password;

	private String firstName;

	private String lastName;

	private String alias;

	private String tenantId;
}
