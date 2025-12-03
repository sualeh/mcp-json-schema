/*
 * MCP JSON Schema
 * Copyright (c) 2025-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.mcp_json_schema;

import static java.util.Objects.requireNonNull;
import static tools.jackson.core.StreamReadFeature.IGNORE_UNDEFINED;
import static tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION;
import static tools.jackson.core.StreamWriteFeature.IGNORE_UNKNOWN;
import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static tools.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static tools.jackson.databind.SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID;

import java.util.logging.Level;
import java.util.logging.Logger;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public class DeserializationUtility {

  private static final Logger LOGGER =
      Logger.getLogger(DeserializationUtility.class.getCanonicalName());

  private static final ObjectMapper mapper = newMapper();

  /**
   * Deserializes a JSON arguments string into an instance of the specified parameters class.
   *
   * <p>Use this at tool execution time to convert the incoming arguments into your strongly typed
   * parameters object or record.
   *
   * <p>Error handling: If deserialization fails, this method returns {@code null} instead of
   * throwing. Callers should check for {@code null} and surface an appropriate error back to the
   * MCP client.
   *
   * @param argumentsString the JSON string received for the tool's arguments; must not be null
   * @param parametersClass the parameters class to instantiate; must not be null
   * @param <P> the parameters type
   * @return a populated instance on success; {@code null} on failure
   */
  public static <P> P instantiateArguments(
      final String argumentsString, final Class<P> parametersClass) {
    try {
      final P argumentsObject = mapper.readValue(argumentsString, parametersClass);
      LOGGER.log(Level.FINER, String.valueOf(argumentsObject));
      return argumentsObject;
    } catch (final Exception e) {
      LOGGER.log(Level.FINE, e, () -> String.format(parametersClass.getName(), argumentsString));
      return null;
    }
  }

  /**
   * Deserializes a JSON {@code argumentsString} into a JSON object node.
   *
   * <p>Use this at tool execution time when you need direct access to the raw JSON object for the
   * incoming arguments, without binding to a specific parameters class.
   *
   * <p>Error handling: If parsing fails, or if the input is not a JSON object (e.g., it's an array
   * or a primitive), this method returns an empty JSON object instead of throwing.
   *
   * @param argumentsString the JSON string received for the tool's arguments; may be null
   * @return a JSON object node representing the parsed arguments; an empty object on error
   */
  public static JsonNode toJsonObject(final String argumentsString) {
    try {
      if (argumentsString == null || argumentsString.isBlank()) {
        return mapper.createObjectNode();
      }
      final JsonNode node = mapper.readTree(argumentsString);
      if (node != null && node.isObject()) {
        return node;
      }
      // Not an object; treat as error and return empty object
      LOGGER.log(Level.FINE, () -> "Expected JSON object for arguments but received: " + node);
      return mapper.createObjectNode();
    } catch (final Exception e) {
      LOGGER.log(
          Level.FINE,
          e,
          () ->
              "Failed to parse arguments JSON; returning empty object. Raw input: "
                  + argumentsString);
      return mapper.createObjectNode();
    }
  }

  private static final ObjectMapper newMapper() {
    final JsonMapper.Builder mapperBuilder = JsonMapper.builder();
    requireNonNull(mapperBuilder, "No mapper builder provided");
    mapperBuilder.enable(ORDER_MAP_ENTRIES_BY_KEYS, INDENT_OUTPUT, USE_EQUALITY_FOR_OBJECT_ID);
    mapperBuilder.enable(INCLUDE_SOURCE_IN_LOCATION, IGNORE_UNDEFINED);
    mapperBuilder.enable(IGNORE_UNKNOWN);
    return mapperBuilder.build();
  }

  private DeserializationUtility() {
    // Prevent instantiation
  }
}
