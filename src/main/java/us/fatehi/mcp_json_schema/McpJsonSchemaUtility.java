/*
 * MCP JSON Schema
 * Copyright (c) 2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.mcp_json_schema;

import java.util.logging.Level;
import java.util.logging.Logger;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class McpJsonSchemaUtility {

  private static final Logger LOGGER =
      Logger.getLogger(McpJsonSchemaUtility.class.getCanonicalName());

  private static final ObjectMapper mapper = new ObjectMapper();

  public static <P> JsonNode generateJsonSchema(final Class<P> clazz) {
    final McpJsonSchemaGenerator generator = new McpJsonSchemaGenerator(mapper);
    return generator.generateJsonSchema(clazz);
  }

  public static <P> String inputSchema(final Class<P> clazz) {
    return generateJsonSchema(clazz).toString();
  }

  public static <P> P instantiateArguments(
      final String argumentsString, final Class<P> parametersClass) {
    try {
      final P argumentsObject = mapper.readValue(argumentsString, parametersClass);
      LOGGER.log(Level.FINE, String.valueOf(argumentsObject));
      return argumentsObject;
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          () ->
              "Function parameters could not be instantiated: %s(%s)"
                  .format(parametersClass.getName(), argumentsString));
      return null;
    }
  }

  private McpJsonSchemaUtility() {
    // Prevent instantiation
  }
}
