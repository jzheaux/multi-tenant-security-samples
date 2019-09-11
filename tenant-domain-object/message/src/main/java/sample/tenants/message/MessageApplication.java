package sample.tenants.message;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import sample.multitenancy.TenantRepository;
import sample.multitenancy.schema.TenantHolderMultiTenantConnectionProvider;
import sample.multitenancy.schema.TenantCurrentTenantIdentifierResolver;
import sample.multitenancy.web.WebClientTenantRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@SpringBootApplication
public class MessageApplication {

	@Value("${tenants-url}") String tenantsUrl;

	@Bean
	TenantRepository tenantRepository() {
		return new WebClientTenantRepository(this.tenantsUrl);
	}

	@Bean
	LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		MultiTenantConnectionProvider connectionProvider = new TenantHolderMultiTenantConnectionProvider(dataSource);
		CurrentTenantIdentifierResolver tenantIdentifierResolver = new TenantCurrentTenantIdentifierResolver();

		HibernateJpaVendorAdapter hibernate = new HibernateJpaVendorAdapter();

		factory.setDataSource(dataSource);
		factory.setJpaVendorAdapter(hibernate);
		factory.setPackagesToScan("sample.tenants.message");

		Map<String, Object> properties = new HashMap<>();
		properties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
		properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
		properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);
		properties.put(Environment.HBM2DDL_AUTO, "create");
		properties.put(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
		factory.setJpaPropertyMap(properties);
		return factory;
	}

	public static void main(String[] args) {
		SpringApplication.run(MessageApplication.class, args);
	}
}
