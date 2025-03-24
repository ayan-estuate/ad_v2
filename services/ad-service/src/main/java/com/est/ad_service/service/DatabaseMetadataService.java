package com.est.ad_service.service;

import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;

public interface DatabaseMetadataService {
    List<String> getTables(JdbcTemplate jdbcTemplate);
    List<String> getViews(JdbcTemplate jdbcTemplate);
    List<Map<String, String>> getColumns(JdbcTemplate jdbcTemplate, String tableName);
    List<String> getPrimaryKeys(JdbcTemplate jdbcTemplate, String tableName);
    List<Map<String, String>> getForeignKeys(JdbcTemplate jdbcTemplate); // Modified return type
    List<String> getIndexes(JdbcTemplate jdbcTemplate, String tableName);
    List<String> getProcedures(JdbcTemplate jdbcTemplate);
    List<String> getTriggers(JdbcTemplate jdbcTemplate);
    List<String> getFunctions(JdbcTemplate jdbcTemplate);
    String getTableComment(JdbcTemplate jdbcTemplate, String tableName); // Added
}