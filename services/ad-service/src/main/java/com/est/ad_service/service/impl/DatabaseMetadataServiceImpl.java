package com.est.ad_service.service.impl;

import com.est.ad_service.exception.MetadataNotFoundException;
import com.est.ad_service.service.DatabaseMetadataService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class DatabaseMetadataServiceImpl implements DatabaseMetadataService {

    @Override
    public List<String> getTables(JdbcTemplate jdbcTemplate) {
        List<String> tables = new ArrayList<>();
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             ResultSet resultSet = connection.getMetaData().getTables(null, null, "%", new String[]{"TABLE"})) {

            while (resultSet.next()) {
                tables.add(resultSet.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            throw new MetadataNotFoundException("Error fetching tables: " + e.getMessage());
        }
        return tables;
    }

    @Override
    public List<String> getViews(JdbcTemplate jdbcTemplate) {
        List<String> views = new ArrayList<>();
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             ResultSet resultSet = connection.getMetaData().getTables(null, null, "%", new String[]{"VIEW"})) {

            while (resultSet.next()) {
                views.add(resultSet.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            throw new MetadataNotFoundException("Error fetching views: " + e.getMessage());
        }
        return views;
    }

    @Override
    public List<Map<String, String>> getColumns(JdbcTemplate jdbcTemplate, String tableName) {
        List<Map<String, String>> columns = new ArrayList<>();
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             ResultSet resultSet = connection.getMetaData().getColumns(null, null, tableName, "%")) {

            while (resultSet.next()) {
                Map<String, String> columnDetails = new LinkedHashMap<>();
                columnDetails.put("COLUMN_NAME", resultSet.getString("COLUMN_NAME"));
                columnDetails.put("DATA_TYPE", resultSet.getString("TYPE_NAME"));
                columnDetails.put("COLUMN_SIZE", resultSet.getString("COLUMN_SIZE"));
                columnDetails.put("IS_NULLABLE", resultSet.getString("IS_NULLABLE"));
                columnDetails.put("COLUMN_DEF", resultSet.getString("COLUMN_DEF"));
                columnDetails.put("IS_AUTOINCREMENT", resultSet.getString("IS_AUTOINCREMENT"));

                columns.add(columnDetails);
            }
        } catch (SQLException e) {
            throw new MetadataNotFoundException("Error fetching columns: " + e.getMessage());
        }
        return columns;
    }

    @Override
    public List<String> getPrimaryKeys(JdbcTemplate jdbcTemplate, String tableName) {
        List<String> primaryKeys = new ArrayList<>();
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             ResultSet resultSet = connection.getMetaData().getPrimaryKeys(null, null, tableName)) {

            while (resultSet.next()) {
                primaryKeys.add(resultSet.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
            throw new MetadataNotFoundException("Error fetching primary keys: " + e.getMessage());
        }
        return primaryKeys;
    }

    @Override
    public List<Map<String, String>> getForeignKeys(JdbcTemplate jdbcTemplate) {
        List<Map<String, String>> foreignKeys = new ArrayList<>();
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // Retrieve all table names
            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");

                    // Get foreign keys for each table
                    try (ResultSet resultSet = metaData.getImportedKeys(null, null, tableName)) {
                        while (resultSet.next()) {
                            Map<String, String> foreignKeyDetails = new LinkedHashMap<>();
                            foreignKeyDetails.put("TABLE_NAME", tableName);
                            foreignKeyDetails.put("FKCOLUMN_NAME", resultSet.getString("FKCOLUMN_NAME"));
                            foreignKeyDetails.put("PKTABLE_NAME", resultSet.getString("PKTABLE_NAME"));
                            foreignKeyDetails.put("PKCOLUMN_NAME", resultSet.getString("PKCOLUMN_NAME"));
                            foreignKeyDetails.put("CONSTRAINT_NAME", resultSet.getString("FK_NAME"));
                            foreignKeys.add(foreignKeyDetails);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new MetadataNotFoundException("Error fetching foreign keys: " + e.getMessage());
        }
        return foreignKeys;
    }



    @Override
    public List<String> getIndexes(JdbcTemplate jdbcTemplate, String tableName) {
        List<String> indexes = new ArrayList<>();
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             ResultSet resultSet = connection.getMetaData().getIndexInfo(null, null, tableName, false, false)) {

            while (resultSet.next()) {
                indexes.add(resultSet.getString("INDEX_NAME"));
            }
        } catch (SQLException e) {
            throw new MetadataNotFoundException("Error fetching indexes: " + e.getMessage());
        }
        return indexes;
    }

    @Override
    public List<String> getProcedures(JdbcTemplate jdbcTemplate) {
        List<String> procedures = new ArrayList<>();
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList("SHOW PROCEDURE STATUS WHERE Db = DATABASE()");
            for (Map<String, Object> row : result) {
                procedures.add(row.get("Name").toString());
            }
        } catch (Exception e) {
            throw new MetadataNotFoundException("Error fetching procedures: " + e.getMessage());
        }
        return procedures;
    }

    @Override
    public List<String> getTriggers(JdbcTemplate jdbcTemplate) {
        List<String> triggers = new ArrayList<>();
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList("SHOW TRIGGERS");
            for (Map<String, Object> row : result) {
                triggers.add(row.get("Trigger").toString());
            }
        } catch (Exception e) {
            throw new MetadataNotFoundException("Error fetching triggers: " + e.getMessage());
        }
        return triggers;
    }

    @Override
    public List<String> getFunctions(JdbcTemplate jdbcTemplate) {
        List<String> functions = new ArrayList<>();
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList("SHOW FUNCTION STATUS WHERE Db = DATABASE()");
            for (Map<String, Object> row : result) {
                functions.add(row.get("Name").toString());
            }
        } catch (Exception e) {
            throw new MetadataNotFoundException("Error fetching functions: " + e.getMessage());
        }
        return functions;
    }

    public String getTableComment(JdbcTemplate jdbcTemplate, String tableName) {
        try {
            String sql = "SELECT table_comment FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?";
            List<String> comments = jdbcTemplate.queryForList(sql, String.class, tableName);

            if (comments.isEmpty()) {
                return ""; // No comment available, return an empty string
            }
            return comments.get(0); // Return the first comment found
        } catch (Exception e) {
            System.err.println("Error fetching table comment for " + tableName + ": " + e.getMessage());
            return ""; // Return empty instead of failing
        }
    }


	
}