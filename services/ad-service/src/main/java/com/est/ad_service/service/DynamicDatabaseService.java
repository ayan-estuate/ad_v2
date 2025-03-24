package com.est.ad_service.service;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
//import javax.sql.DataSource;

@Service
public class DynamicDatabaseService {

    public JdbcTemplate connectToDatabase(String url, String username, String password) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setMaximumPoolSize(10);

        return new JdbcTemplate(dataSource);
    }
}

