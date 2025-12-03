/*
 * MCP JSON Schema
 * Copyright (c) 2025-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.mcp_json_schema;

import java.util.logging.Logger;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Convenience utilities for working with the Model Context Protocol (MCP) JSON Schema.
 *
 * <p>This class provides two primary capabilities:
 *
 * <ul>
 *   <li>Generate an MCP-compliant JSON Schema for a Jackson annotated Java type that represents a
 *       tool's input parameters.
 *   <li>Instantiate an instance of that parameters type from a JSON arguments string received at
 *       tool invocation time.
 * </ul>
 *
 * When to use:
 *
 * <ul>
 *   <li>At tool registration time, call {@link #inputSchema(Class)} (or {@link
 *       #generateJsonSchema(Class)}) to advertise the tool's <code>input_schema</code> to MCP
 *       clients.
 *   <li>At tool execution time, call {@link DeserializationUtility#instantiateArguments(String,
 *       Class)} to parse the incoming JSON <em>arguments</em> into your parameters object or
 *       record.
 * </ul>
 *
 * <p>The generated schema targets the MCP JSON Schema subset (see the <a href=
 * "https://modelcontextprotocol.io/specification/2025-06-18/schema#primitiveschemadefinition">MCP
 * Schema specification</a>). Supported annotations include common Jackson metadata such as
 * {@code @JsonProperty(required = true, defaultValue = "...")}, {@code @JsonPropertyDescription},
 * enums, arrays/ collections, and primitive/ boxed types.
 *
 * <p>Thread-safety: this utility maintains a single shared {@link ObjectMapper}, which is safe to
 * use concurrently once configured. The mapper uses default settings.
 */
public class McpJsonSchemaUtility {

  static final Logger LOGGER = Logger.getLogger(McpJsonSchemaUtility.class.getCanonicalName());

  static final ObjectMapper mapper = new ObjectMapper();

  /**
   * Generates an MCP JSON Schema for the supplied parameters class.
   *
   * <p>Intended for use when building the <code>input_schema</code> portion of a tool definition.
   * The result conforms to the MCP subset,
   *
   * @param clazz the Jackson‑annotated parameters type (record, POJO, or bean). Must not be null.
   * @param <P> the parameters type
   * @return a {@link JsonNode} containing the schema
   * @throws NullPointerException if {@code clazz} is null
   */
  public static <P> JsonNode generateJsonSchema(final Class<P> clazz) {
    final McpJsonSchemaGenerator generator = new McpJsonSchemaGenerator(mapper);
    return generator.generateJsonSchema(clazz);
  }

  /**
   * Generates an MCP JSON Schema as a compact JSON string.
   *
   * <p>Useful for directly embedding into a tool definition's <code>input_schema</code> field.
   *
   * @param clazz the parameters type
   * @param <P> the parameters type
   * @return the schema serialized as JSON text
   * @throws NullPointerException if {@code clazz} is null
   */
  public static <P> String inputSchema(final Class<P> clazz) {
    return generateJsonSchema(clazz).toString();
  }

  private McpJsonSchemaUtility() {
    // Prevent instantiation
  }
}
