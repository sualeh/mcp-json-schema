/*
 * MCP JSON Schema
 * Copyright (c) 2025-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record SampleParameters(
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
