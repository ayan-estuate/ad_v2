package com.est.ad_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.est.ad_service.dto.DatabaseConnectionDTO;
import com.est.ad_service.dto.CombinedPromptRequestDTO;
import com.est.ad_service.service.ADScriptGeneratorService;
import com.est.ad_service.service.DynamicDatabaseService;

@RestController
@RequestMapping("/api/adscript")
public class ADScriptController {

    private final DynamicDatabaseService dynamicDatabaseService;
    private final ADScriptGeneratorService adScriptService;

    public ADScriptController(DynamicDatabaseService dynamicDatabaseService, ADScriptGeneratorService adScriptService) {
        this.dynamicDatabaseService = dynamicDatabaseService;
        this.adScriptService = adScriptService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateADScript(@RequestParam String archiveName, @RequestBody DatabaseConnectionDTO request) {
        JdbcTemplate jdbcTemplate = dynamicDatabaseService.connectToDatabase(
                request.url(),
                request.username(),
                request.password()
        );
        String scriptPath = adScriptService.generateADScript(jdbcTemplate, archiveName);
        return ResponseEntity.ok(scriptPath);
    }

    @PostMapping("/createPrompt")
    public ResponseEntity<String> createPrompt(@RequestBody CombinedPromptRequestDTO request) {
        JdbcTemplate jdbcTemplate = dynamicDatabaseService.connectToDatabase(
                request.url(),
                request.username(),
                request.password()
        );

        String insertQuery = "INSERT INTO config_prompt.prompt_configurations (table_name, column_name, prompt_text, data_type) VALUES (?, ?, ?, ?)";
        int rowsAffected = jdbcTemplate.update(insertQuery, request.tableName(), request.columnName(), request.promptText(), request.dataType());

        if (rowsAffected > 0) {
            return ResponseEntity.ok("Prompt configuration created successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create prompt configuration.");
        }
    }
}