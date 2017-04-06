package com.example;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataSourceBasedMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final long serialVersionUID = 8168907057647334460L;

    @Autowired
    private MultitenancyProperties multitenancyProperties;

    private Map<String, DataSource> map;

    @PostConstruct
    public void load() {
        map = multitenancyProperties.getTenants().stream()
                .collect(Collectors.toMap(o -> o.getName(), o -> {
                    return DataSourceBuilder.create(o.getClassLoader())
                            .type(com.zaxxer.hikari.HikariDataSource.class)
                            .driverClassName(o.getDriverClassName())
                            .username(o.getUsername())
                            .password(o.getPassword())
                            .url(o.getUrl())
                            .build();
                }));
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return map.get(multitenancyProperties.getDefaultTenant());
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return map.get(tenantIdentifier);
    }
}
