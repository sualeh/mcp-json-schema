/*
 * MCP JSON Schema
 * Copyright (c) 2025-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.mcp_json_schema.utility;

import static us.fatehi.mcp_json_schema.utility.JsonUtility.mapper;

import java.util.logging.Level;
import java.util.logging.Logger;
import tools.jackson.databind.JsonNode;

public class DeserializationUtility {

  private static final Logger LOGGER =
      Logger.getLogger(DeserializationUtility.class.getCanonicalName());

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
    if (parametersClass == null) {
      LOGGER.log(Level.FINER, "No parameters class provided");
      return null;
    }
    if (argumentsString == null || argumentsString.isBlank()) {
      LOGGER.log(Level.FINER, "No arguments provided");
      return null;
    }
    try {
      final P argumentsObject = mapper.readValue(argumentsString, parametersClass);
      LOGGER.log(Level.FINER, () -> String.valueOf(argumentsObject));
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

  private DeserializationUtility() {
    // Prevent instantiation
  }
}
