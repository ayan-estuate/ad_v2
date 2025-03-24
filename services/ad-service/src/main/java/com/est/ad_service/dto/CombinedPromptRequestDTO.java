package com.est.ad_service.dto;

public record CombinedPromptRequestDTO(
        String url,
        String username,
        String password,
        String tableName,
        String columnName,
        String promptText,
        String dataType
) {
}