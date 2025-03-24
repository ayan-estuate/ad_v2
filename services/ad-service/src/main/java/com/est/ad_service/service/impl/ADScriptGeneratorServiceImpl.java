package com.est.ad_service.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.est.ad_service.service.ADScriptGeneratorService;
import com.est.ad_service.service.DatabaseMetadataService;

@Service
public class ADScriptGeneratorServiceImpl implements ADScriptGeneratorService {

    private final DatabaseMetadataService metadataService;

    public ADScriptGeneratorServiceImpl(DatabaseMetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @Override
    public String generateADScript(JdbcTemplate jdbcTemplate, String archiveName) {
        String sanitizedArchiveName = archiveName.replaceAll("[^a-zA-Z0-9_-]", "_");
        String outputDir = "D:\\datadir\\ad_scripts";

        File directory = new File(outputDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName;
        try {
            fileName = Paths.get(outputDir, sanitizedArchiveName + "_AD_Script.txt").toString();
        } catch (InvalidPathException e) {
            return "Invalid archive name or path.";
        }

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("CREATE AD " + sanitizedArchiveName + ".AD" + sanitizedArchiveName + "\n");
            writer.write("  DESC //'Generated Access Definition'//\n");
            writer.write("  SRCQUAL YourSourceQualifier START YourStartTable ADDTBLS N\n");
            writer.write("  MODCRIT N ADCHGS N USENEW N USEFM N PNSSTATE N\n\n");

            // **VARIABLES (Prompts)**
            writer.write("-- VARIABLES --\n");
            List<Map<String, Object>> promptConfigs = jdbcTemplate.queryForList("SELECT table_name, column_name, prompt_text, data_type FROM config_prompt.prompt_configurations");

            for (Map<String, Object> promptConfig : promptConfigs) {
                String columnName = (String) promptConfig.get("column_name");
                String promptText = (String) promptConfig.get("prompt_text");
                String dataType = (String) promptConfig.get("data_type");

                String adDataType = switch (dataType.toUpperCase()) {
                    case "INT", "INTEGER" -> "INT";
                    case "DATE", "DATETIME" -> "DATE";
                    default -> "VARCHAR";
                };

                writer.write("  VAR (" + columnName + " PRMPT //'" + promptText + "'// DATATYPE " + adDataType + ")\n");
            }
            writer.write("\n");

            // **TABLE DEFINITIONS**
            writer.write("-- TABLE DEFINITIONS --\n");
            List<String> tables = metadataService.getTables(jdbcTemplate);
            for (String table : tables) {
                String tableComment = metadataService.getTableComment(jdbcTemplate, table);
                String escapedTableComment = (tableComment != null) ? tableComment.replace("'", "''") : "";

                writer.write("  TABLE (" + table + " ACCESS SUID REF N DAA Y UR N PREDOP A VARDELIM : COLFLAG N EXTRROWID N DESC //'" + escapedTableComment + "'//)\n");
            }
            writer.write("\n");

            // **RELATIONSHIPS**
            writer.write("-- RELATIONSHIPS --\n");
            List<Map<String, String>> foreignKeys = metadataService.getForeignKeys(jdbcTemplate);
            for (Map<String, String> foreignKey : foreignKeys) {
                String childTable = foreignKey.get("FKTABLE_NAME");
                String fkColumn = foreignKey.get("FKCOLUMN_NAME");
                String pkTable = foreignKey.get("PKTABLE_NAME");
                String pkColumn = foreignKey.get("PKCOLUMN_NAME");
                String constraintName = foreignKey.get("CONSTRAINT_NAME");

                // Generate a unique relationship name
                String relationshipName = "REL_" + constraintName.replaceAll("[^a-zA-Z0-9]", "_");

                writer.write("  REL (//" + relationshipName + "//\n");
                writer.write("    STATUS UNK USAGE S Q1 N Q2 N LIMIT 0 TYPE PST\n");
                writer.write("    PAR " + pkTable + " PAR_ACCESS D PAR_KEYLIMIT 1\n");
                writer.write("    CHI " + childTable + " CHI_ACCESS D CHI_KEYLIMIT 1\n");
                writer.write("    JOIN (" + childTable + "." + fkColumn + " = " + pkTable + "." + pkColumn + ")\n");
                writer.write("  )\n");
            }
            writer.write("\n");

            // **ARCHACTS (Archive Actions)**
            writer.write("-- ARCHACTS (Archive Actions) --\n");
            writer.write("   ARCHACTS (ACTION EEP DBALIAS YourDBAlias SQL //\"Your Stored Procedure Call Here\"// ADVAR $)\n");
            writer.write(";\n");

            return "AD Script generated: " + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error generating AD script.";
        }
    }
}
