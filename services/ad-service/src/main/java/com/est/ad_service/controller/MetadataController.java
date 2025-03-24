package com.est.ad_service.controller;

import com.est.ad_service.service.DatabaseMetadataService;
import com.est.ad_service.service.DynamicDatabaseService;
import org.springframework.http.ResponseEntity;
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

    private JdbcTemplate getJdbcTemplate(Map<String, String> dbDetails) {
        String url = dbDetails.get("url");
        String username = dbDetails.get("username");
        String password = dbDetails.get("password");

        if (url == null || username == null || password == null) {
            throw new IllegalArgumentException("Database connection details are missing.");
        }
        return dynamicDatabaseService.connectToDatabase(url, username, password);
    }

    @PostMapping("/tables")
    public ResponseEntity<List<String>> getTables(@RequestBody Map<String, String> dbDetails) {
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(dbDetails);
            List<String> tables = databaseMetadataService.getTables(jdbcTemplate);
            return tables.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tables);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/views")
    public ResponseEntity<List<String>> getViews(@RequestBody Map<String, String> dbDetails) {
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(dbDetails);
            List<String> views = databaseMetadataService.getViews(jdbcTemplate);
            return views.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(views);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/columns")
    public ResponseEntity<List<Map<String, String>>> getColumns(@RequestBody Map<String, String> dbDetails, @RequestParam String tableName) {
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(dbDetails);
            List<Map<String, String>> columns = databaseMetadataService.getColumns(jdbcTemplate, tableName);
            return columns.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(columns);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/primary-keys")
    public ResponseEntity<List<String>> getPrimaryKeys(@RequestBody Map<String, String> dbDetails, @RequestParam String tableName) {
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(dbDetails);
            List<String> primaryKeys = databaseMetadataService.getPrimaryKeys(jdbcTemplate, tableName);
            return primaryKeys.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(primaryKeys);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/foreign-keys")
    public ResponseEntity<List<Map<String, String>>> getForeignKeys(@RequestBody Map<String, String> dbDetails) {
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(dbDetails);
            List<Map<String, String>> foreignKeys = databaseMetadataService.getForeignKeys(jdbcTemplate);
            return foreignKeys.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(foreignKeys);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/procedures")
    public ResponseEntity<List<String>> getProcedures(@RequestBody Map<String, String> dbDetails) {
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(dbDetails);
            List<String> procedures = databaseMetadataService.getProcedures(jdbcTemplate);
            return procedures.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(procedures);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/indexes")
    public ResponseEntity<List<String>> getIndexes(@RequestBody Map<String, String> dbDetails, @RequestParam String tableName) {
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(dbDetails);
            List<String> indexes = databaseMetadataService.getIndexes(jdbcTemplate, tableName);
            return indexes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(indexes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
