package com.est.ad_service.controller;


import com.est.ad_service.service.DatabaseMetadataService;
import com.est.ad_service.service.DynamicDatabaseService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metadata")
public class MetadataController {

    private final DynamicDatabaseService dynamicDatabaseService;
    private final DatabaseMetadataService databaseMetadataService;

    public MetadataController(DynamicDatabaseService dynamicDatabaseService, DatabaseMetadataService databaseMetadataService) {
        this.dynamicDatabaseService = dynamicDatabaseService;
        this.databaseMetadataService = databaseMetadataService;
    }

    @PostMapping("/tables")
    public List<String> getTables(@RequestBody Map<String, String> dbDetails) {
        JdbcTemplate jdbcTemplate = dynamicDatabaseService.connectToDatabase(dbDetails.get("url"), dbDetails.get("username"), dbDetails.get("password"));
        return databaseMetadataService.getTables(jdbcTemplate);
    }

    @PostMapping("/views")
    public List<String> getViews(@RequestBody Map<String, String> dbDetails) {
        JdbcTemplate jdbcTemplate = dynamicDatabaseService.connectToDatabase(dbDetails.get("url"), dbDetails.get("username"), dbDetails.get("password"));
        return databaseMetadataService.getViews(jdbcTemplate);
    }

    @PostMapping("/columns")
    public List<Map<String, String>> getColumns(@RequestBody Map<String, String> dbDetails, @RequestParam String tableName) {
        JdbcTemplate jdbcTemplate = dynamicDatabaseService.connectToDatabase(dbDetails.get("url"), dbDetails.get("username"), dbDetails.get("password"));
        return databaseMetadataService.getColumns(jdbcTemplate, tableName);
    }

    @PostMapping("/primary-keys")
    public List<String> getPrimaryKeys(@RequestBody Map<String, String> dbDetails, @RequestParam String tableName) {
        JdbcTemplate jdbcTemplate = dynamicDatabaseService.connectToDatabase(dbDetails.get("url"), dbDetails.get("username"), dbDetails.get("password"));
        return databaseMetadataService.getPrimaryKeys(jdbcTemplate, tableName);
    }

    @PostMapping("/foreign-keys")
    public List<String> getForeignKeys(@RequestBody Map<String, String> dbDetails, @RequestParam String tableName) {
        JdbcTemplate jdbcTemplate = dynamicDatabaseService.connectToDatabase(dbDetails.get("url"), dbDetails.get("username"), dbDetails.get("password"));
        return databaseMetadataService.getForeignKeys(jdbcTemplate, tableName);
    }

    @PostMapping("/procedures")
    public List<String> getProcedures(@RequestBody Map<String, String> dbDetails) {
        JdbcTemplate jdbcTemplate = dynamicDatabaseService.connectToDatabase(dbDetails.get("url"), dbDetails.get("username"), dbDetails.get("password"));
        return databaseMetadataService.getProcedures(jdbcTemplate);
    }

    @PostMapping("/indexes")
    public List<String> getIndexes(@RequestBody Map<String, String> dbDetails, @RequestParam String tableName) {
        JdbcTemplate jdbcTemplate = dynamicDatabaseService.connectToDatabase(dbDetails.get("url"), dbDetails.get("username"), dbDetails.get("password"));
        return databaseMetadataService.getIndexes(jdbcTemplate, tableName);
    }
}

