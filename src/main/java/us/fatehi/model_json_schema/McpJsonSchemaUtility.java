/*
 * MCP JSON Schema
 * Copyright (c) 2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.model_json_schema;

import tools.jackson.databind.JsonNode;

public class McpJsonSchemaUtility {

  public static JsonNode generateJsonSchema(final Class<?> clazz) {
    return McpJsonSchemaGenerator.generateJsonSchema(clazz);
  }

  private McpJsonSchemaUtility() {
    // Prevent instantiation
  }
}
