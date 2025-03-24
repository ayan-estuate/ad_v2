package com.est.ad_service.service;

import org.springframework.jdbc.core.JdbcTemplate;

public interface ADScriptGeneratorService {
    String generateADScript(JdbcTemplate jdbcTemplate, String archiveName);
    
}
