package com.example;

import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties("app.multitenancy")
@Data
public class MultitenancyProperties {
    private List<DataSourceProperties> tenants = new ArrayList<>();
}
