package com.est.ad_service.controller;


import com.est.ad_service.service.ADScriptGeneratorService;
import com.est.ad_service.service.DynamicDatabaseService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public String generateADScript(@RequestBody Map<String, String> dbDetails, @RequestParam String archiveName) {
        JdbcTemplate jdbcTemplate = dynamicDatabaseService.connectToDatabase(dbDetails.get("url"), dbDetails.get("username"), dbDetails.get("password"));
        return adScriptService.generateADScript(jdbcTemplate, archiveName);
    }
}

