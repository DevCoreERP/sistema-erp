package com.devcoreerp.backend_erp.multitenancy;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider<String> {

    private final DataSource dataSource;

    public MultiTenantConnectionProviderImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        setSearchPath(connection, TenantConstants.PUBLIC_SCHEMA);
        return connection;
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        resetAndClose(connection);
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection connection = dataSource.getConnection();
        setSearchPath(connection, resolveTenantIdentifier(tenantIdentifier));
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        resetAndClose(connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return unwrapType.isAssignableFrom(getClass()) || unwrapType.isAssignableFrom(DataSource.class);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (unwrapType.isAssignableFrom(getClass())) {
            return unwrapType.cast(this);
        }
        if (unwrapType.isAssignableFrom(DataSource.class)) {
            return unwrapType.cast(dataSource);
        }
        return null;
    }

    private String resolveTenantIdentifier(String tenantIdentifier) {
        if (tenantIdentifier == null || tenantIdentifier.isBlank()) {
            return TenantConstants.PUBLIC_SCHEMA;
        }
        return tenantIdentifier;
    }

    private void resetAndClose(Connection connection) throws SQLException {
        try {
            setSearchPath(connection, TenantConstants.PUBLIC_SCHEMA);
        } finally {
            connection.close();
        }
    }

    private void setSearchPath(Connection connection, String schemaName) throws SQLException {
        String validatedSchema = TenantValidator.validateSchemaName(schemaName);
        try (Statement statement = connection.createStatement()) {
            if (TenantConstants.PUBLIC_SCHEMA.equals(validatedSchema)) {
                statement.execute("SET search_path TO public");
            } else {
                statement.execute("SET search_path TO " + TenantValidator.quotedSchemaName(validatedSchema) + ", public");
            }
        }
    }
}
