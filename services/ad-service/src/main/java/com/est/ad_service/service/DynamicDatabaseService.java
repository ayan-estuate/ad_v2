package com.est.ad_service.service;

import org.springframework.jdbc.core.JdbcTemplate;

public interface DynamicDatabaseService {
    JdbcTemplate connectToDatabase(String url, String username, String password);
}
