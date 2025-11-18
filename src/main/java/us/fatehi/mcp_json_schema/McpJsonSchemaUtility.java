/*
 * MCP JSON Schema
 * Copyright (c) 2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.mcp_json_schema;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class McpJsonSchemaUtility {

  public static JsonNode generateJsonSchema(final Class<?> clazz) {
    final McpJsonSchemaGenerator generator = new McpJsonSchemaGenerator(new ObjectMapper());
    return generator.generateJsonSchema(clazz);
  }

  public static String inputSchema(final Class<?> clazz) {
    return generateJsonSchema(clazz).toString();
  }

  private McpJsonSchemaUtility() {
    // Prevent instantiation
  }
}
