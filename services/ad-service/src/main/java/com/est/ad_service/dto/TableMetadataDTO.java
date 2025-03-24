package com.est.ad_service.dto;

import java.util.List;
import java.util.Map;

public record TableMetadataDTO(List<String> tables, List<String> views, List<Map<String, String>> columns) {
}
