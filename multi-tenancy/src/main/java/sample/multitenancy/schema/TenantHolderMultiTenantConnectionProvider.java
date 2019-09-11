package sample.multitenancy.schema;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import sample.multitenancy.Tenant;

public class TenantHolderMultiTenantConnectionProvider implements MultiTenantConnectionProvider {

	private final DataSource dataSource;

	public TenantHolderMultiTenantConnectionProvider(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Connection getAnyConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		connection.close();
	}

	@Override
	public Connection getConnection(String tenantIdentifier) throws SQLException {
		final Connection connection = getAnyConnection();
		try {
			connection.createStatement().execute( "USE " + tenantIdentifier );
		}
		catch ( SQLException e ) {
			throw new HibernateException(
					"Could not alter JDBC connection to specified schema [" + tenantIdentifier + "]",
					e
			);
		}
		return connection;
	}

	@Override
	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
		try {
			connection.createStatement().execute( "USE " + Tenant.DEFAULT_TENANT );
		}
		catch ( SQLException e ) {
			throw new HibernateException(
					"Could not alter JDBC connection to specified schema [" + tenantIdentifier + "]",
					e
			);
		}
		connection.close();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isUnwrappableAs(Class unwrapType) {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> unwrapType) {
		return null;
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return true;
	}

}
