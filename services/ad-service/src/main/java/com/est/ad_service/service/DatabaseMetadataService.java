package com.est.ad_service.service;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseMetadataService {

    public List<String> getTables(JdbcTemplate jdbcTemplate) {
        List<String> tables = new ArrayList<>();
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             ResultSet resultSet = connection.getMetaData().getTables(null, null, "%", new String[]{"TABLE"})) {

            while (resultSet.next()) {
                tables.add(resultSet.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    public List<String> getPrimaryKeys(JdbcTemplate jdbcTemplate, String tableName) {
        List<String> primaryKeys = new ArrayList<>();
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             ResultSet resultSet = connection.getMetaData().getPrimaryKeys(null, null, tableName)) {

            while (resultSet.next()) {
                primaryKeys.add(resultSet.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return primaryKeys;
    }

    public List<String> getForeignKeys(JdbcTemplate jdbcTemplate, String tableName) {
        List<String> foreignKeys = new ArrayList<>();
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             ResultSet resultSet = connection.getMetaData().getImportedKeys(null, null, tableName)) {

            while (resultSet.next()) {
                String fkColumn = resultSet.getString("FKCOLUMN_NAME");
                String referencedTable = resultSet.getString("PKTABLE_NAME");
                String referencedColumn = resultSet.getString("PKCOLUMN_NAME");
                foreignKeys.add(fkColumn + " -> " + referencedTable + "." + referencedColumn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foreignKeys;
    }

    public List<String> getIndexes(JdbcTemplate jdbcTemplate, String tableName) {
        List<String> indexes = new ArrayList<>();
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             ResultSet resultSet = connection.getMetaData().getIndexInfo(null, null, tableName, false, false)) {

            while (resultSet.next()) {
                indexes.add(resultSet.getString("INDEX_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return indexes;
    }

    public List<String> getSequences(JdbcTemplate jdbcTemplate) {
        List<String> sequences = new ArrayList<>();
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(
                    "SELECT TABLE_NAME, COLUMN_NAME FROM information_schema.COLUMNS WHERE EXTRA LIKE '%auto_increment%'");
            for (Map<String, Object> row : result) {
                sequences.add(row.get("TABLE_NAME") + " (" + row.get("COLUMN_NAME") + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sequences;
    }


    public List<String> getTriggers(JdbcTemplate jdbcTemplate) {
        List<String> triggers = new ArrayList<>();
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList("SHOW TRIGGERS");
            for (Map<String, Object> row : result) {
                triggers.add(row.get("Trigger").toString()); // Extracting only trigger names
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return triggers;
    }


    public List<String> getProcedures(JdbcTemplate jdbcTemplate) {
        List<String> procedures = new ArrayList<>();
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList("SHOW PROCEDURE STATUS WHERE Db = DATABASE()");
            for (Map<String, Object> row : result) {
                procedures.add(row.get("Name").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return procedures;
    }

    public List<String> getFunctions(JdbcTemplate jdbcTemplate) {
        List<String> functions = new ArrayList<>();
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList("SHOW FUNCTION STATUS WHERE Db = DATABASE()");
            for (Map<String, Object> row : result) {
                functions.add(row.get("Name").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return functions;
    }
    public List<Map<String, String>> getColumns(JdbcTemplate jdbcTemplate, String tableName) {
        List<Map<String, String>> columns = new ArrayList<>();
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             ResultSet resultSet = connection.getMetaData().getColumns(null, null, tableName, "%")) {

            while (resultSet.next()) {
                Map<String, String> columnDetails = new LinkedHashMap<>();
                columnDetails.put("Column Name", resultSet.getString("COLUMN_NAME"));
                columnDetails.put("Data Type", resultSet.getString("TYPE_NAME"));
                columnDetails.put("Column Size", resultSet.getString("COLUMN_SIZE"));
                columnDetails.put("Is Nullable", resultSet.getString("IS_NULLABLE"));
                columnDetails.put("Default Value", resultSet.getString("COLUMN_DEF"));
                columnDetails.put("Is Auto Increment", resultSet.getString("IS_AUTOINCREMENT"));
                columnDetails.put("Decimal Digits", resultSet.getString("DECIMAL_DIGITS"));
                columnDetails.put("Remarks", resultSet.getString("REMARKS")); // Comment on the column

                columns.add(columnDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columns;
    }

    public List<String> getViews(JdbcTemplate jdbcTemplate) {
        List<String> views = new ArrayList<>();
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             ResultSet resultSet = connection.getMetaData().getTables(null, null, "%", new String[]{"VIEW"})) {

            while (resultSet.next()) {
                views.add(resultSet.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return views;
    }

}

