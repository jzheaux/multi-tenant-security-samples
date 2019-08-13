package sample.multitenancy.schema;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.internal.SchemaCreatorImpl;
import org.hibernate.tool.schema.internal.SchemaDropperImpl;
import org.hibernate.tool.schema.internal.exec.JdbcContext;
import org.hibernate.tool.schema.spi.SchemaCreator;
import org.hibernate.tool.schema.spi.SchemaDropper;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.hibernate.tool.schema.spi.SchemaMigrator;
import org.hibernate.tool.schema.spi.SchemaValidator;

public class MultiTenantSchemaManagementTool implements SchemaManagementTool, ServiceRegistryAwareService {
	List<String> tenants;
	HibernateSchemaManagementTool tool = new HibernateSchemaManagementTool();

	public MultiTenantSchemaManagementTool(List<String> tenants) {
		this.tenants = tenants;
	}

	@Override
	public SchemaCreator getSchemaCreator(Map options) {
		return (metadata, options1, sourceDescriptor, targetDescriptor) -> {
			JdbcContext context = tool.resolveJdbcContext(options);
			DdlTransactionIsolator isolator = tool.getDdlTransactionIsolator(context);
			SchemaCreator creator = new SchemaCreatorImpl(this.tool);

			for (String tenant : tenants) {
				createSchemaIfNecessary(isolator, tenant);
				prepareTenant(isolator, tenant);
				creator.doCreation(metadata, options1, sourceDescriptor, targetDescriptor);
			}

			isolator.release();
		};
	}

	@Override
	public SchemaDropper getSchemaDropper(Map options) {
		return new SchemaDropperImpl(this.tool);
	}

	@Override
	public SchemaMigrator getSchemaMigrator(Map options) {
		throw new UnsupportedOperationException("unsupported");
	}

	@Override
	public SchemaValidator getSchemaValidator(Map options) {
		throw new UnsupportedOperationException("unsupported");
	}

	private void createSchemaIfNecessary(DdlTransactionIsolator isolator, String tenant) {
		try {
			Statement stmt = isolator.getIsolatedConnection().createStatement();
			stmt.execute("CREATE SCHEMA IF NOT EXISTS " + tenant);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	private void prepareTenant(DdlTransactionIsolator isolator, String tenant) {
		try {
			Statement stmt = isolator.getIsolatedConnection().createStatement();
			stmt.execute("SET SCHEMA " + tenant);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
		this.tool.injectServices(serviceRegistry);
	}
}
