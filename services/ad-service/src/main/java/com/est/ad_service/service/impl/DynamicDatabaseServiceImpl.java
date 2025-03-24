package com.est.ad_service.service.impl;

import com.est.ad_service.exception.DatabaseConnectionException;
import com.est.ad_service.service.DynamicDatabaseService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DynamicDatabaseServiceImpl implements DynamicDatabaseService {

    @Override
    public JdbcTemplate connectToDatabase(String url, String username, String password) {
        try {
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setMaximumPoolSize(10);

            return new JdbcTemplate(dataSource);
        } catch (Exception e) {
            throw new DatabaseConnectionException("Failed to connect to database: " + e.getMessage());
        }
    }
}
