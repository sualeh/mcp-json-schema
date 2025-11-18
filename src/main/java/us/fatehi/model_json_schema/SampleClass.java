/*
 * MCP JSON Schema
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.model_json_schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record SampleClass(
    @JsonPropertyDescription("Type of database table dependant objects.")
        @JsonProperty(defaultValue = "NONE", required = true)
        DependantObjectType dependantObjectType,
    @JsonPropertyDescription("Table name.") String tableName) {

  public enum DependantObjectType {
    NONE,
    COLUMNS,
    INDEXES,
    FOREIGN_KEYS,
    TRIGGERS;
  }
}
