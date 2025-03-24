package com.est.ad_service.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;
import java.util.List;

@Service
public class ADScriptGeneratorService {

    private final DatabaseMetadataService metadataService;

    public ADScriptGeneratorService(DatabaseMetadataService metadataService) {
        this.metadataService = metadataService;
    }

    public String generateADScript(JdbcTemplate jdbcTemplate, String archiveName) {
        // Sanitize archive name to prevent invalid characters
        String sanitizedArchiveName = archiveName.replaceAll("[^a-zA-Z0-9_-]", "_");
        String outputDir = "D:\\ad_scripts"; // Use escaped backslashes

        // Ensure the directory exists
        File directory = new File(outputDir);
        if (!directory.exists()) {
            directory.mkdirs();  // Create directory if it does not exist
        }

        String fileName;
        try {
            fileName = Paths.get(outputDir, sanitizedArchiveName + "_AD_Script.txt").toString();
        } catch (InvalidPathException e) {
            return "Invalid archive name or path.";
        }

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("CREATE AD " + archiveName + "\n");
            writer.write("DESC //'Generated Access Definition'//\n\n");

            List<String> tables = metadataService.getTables(jdbcTemplate);
            for (String table : tables) {
                writer.write("TABLE (" + table + " ACCESS SUID REF N DAA Y UR N PREDOP A VARDELIM : COLFLAG N EXTRROWID N)\n");
            }

            writer.write("\n-- PRIMARY KEYS --\n");
            for (String table : tables) {
                writer.write("PRIMARY KEYS (" + table + " -> " + String.join(", ", metadataService.getPrimaryKeys(jdbcTemplate, table)) + ")\n");
            }

            writer.write("\n-- FOREIGN KEYS --\n");
            for (String table : tables) {
                writer.write("FOREIGN KEYS (" + table + " -> " + String.join(", ", metadataService.getForeignKeys(jdbcTemplate, table)) + ")\n");
            }

            writer.write("\n-- INDEXES --\n");
            for (String table : tables) {
                writer.write("INDEXES (" + table + " -> " + String.join(", ", metadataService.getIndexes(jdbcTemplate, table)) + ")\n");
            }

            writer.write("\n-- SEQUENCES --\n");
            writer.write(String.join("\n", metadataService.getSequences(jdbcTemplate)) + "\n");

            writer.write("\n-- TRIGGERS --\n");
            writer.write(String.join("\n", metadataService.getTriggers(jdbcTemplate)) + "\n");

            writer.write("\n-- PROCEDURES --\n");
            writer.write(String.join("\n", metadataService.getProcedures(jdbcTemplate)) + "\n");

            writer.write("\n-- FUNCTIONS --\n");
            writer.write(String.join("\n", metadataService.getFunctions(jdbcTemplate)) + "\n");

            return "AD Script generated at: " + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error generating AD script: " + e.getMessage();
        }
    }
}
