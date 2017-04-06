package com.example;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataSourceBasedMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final long serialVersionUID = 8168907057647334460L;
    private static final String DEFAULT_TENANT_ID = "tenant_1";

    @Autowired
    private MultitenancyProperties multitenancyProperties;

    private Map<String, DataSource> map;

    @PostConstruct
    public void load() {
        map = multitenancyProperties.getTenants().stream()
                .collect(Collectors.toMap(o -> o.getName(), o -> {
                    return DataSourceBuilder.create(o.getClassLoader())
                            .driverClassName(o.getDriverClassName())
                            .username(o.getUsername())
                            .password(o.getPassword())
                            .url(o.getUrl())
                            .build();
                }));
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return map.get(DEFAULT_TENANT_ID);
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return map.get(tenantIdentifier);
    }
}
