/*
 * MCP JSON Schema
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.mcp_json_schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JsonSchemaGenerator {

  private static final ObjectMapper mapper = new ObjectMapper();

  public static JsonNode generateSchema(final Class<?> clazz) throws Exception {

    final SerializationConfig config = mapper.getSerializationConfig();
    final BeanDescription beanDesc =
        config.introspect(TypeFactory.defaultInstance().constructType(clazz));

    final ObjectNode schemaNode = mapper.createObjectNode();

    schemaNode.put("type", "object");
    final ObjectNode propertiesNode = schemaNode.putObject("properties");
    final List<String> required = new ArrayList<>();
    final List<BeanPropertyDefinition> propertyDefinitions = beanDesc.findProperties();
    for (final BeanPropertyDefinition propertyDefinition : propertyDefinitions) {
      System.out.println(propertyDefinition.getGetter().getFullName());
      final String propertyName = propertyDefinition.getName();
      final ObjectNode parameterSchema = propertiesNode.putObject(propertyName);

      final Class<?> propertyClass = propertyDefinition.getPrimaryType().getRawClass();
      final String typeName = mapJavaTypeToJsonType(propertyClass);
      parameterSchema.put("type", typeName);

      final AnnotatedMember accessor = propertyDefinition.getAccessor();

      if (accessor.hasAnnotation(JsonPropertyDescription.class)) {
        final JsonPropertyDescription propertyDescription =
            accessor.getAnnotation(JsonPropertyDescription.class);
        parameterSchema.put(
            "description", propertyDescription.value().replaceAll("\\R", " ").strip());
      }

      final List<String> enumValues = getEnumValues(propertyClass);
      if (!enumValues.isEmpty()) {
        final ArrayNode enumValuesNode = parameterSchema.putArray("enum");
        enumValues.forEach(enumValuesNode::add);
      }

      if (accessor.hasAnnotation(JsonProperty.class)) {
        final JsonProperty jsonProperty = accessor.getAnnotation(JsonProperty.class);
        if (jsonProperty.required()) {
          required.add(propertyName);
        }
      }
    }

    final ArrayNode requiredArray = schemaNode.putArray("required");
    required.forEach(requiredArray::add);

    schemaNode.put("additionalProperties", false);

    return schemaNode;
  }

  private static List<String> getEnumValues(final Class<?> type) {
    if (type.isEnum()) {
      final Object[] constants = type.getEnumConstants();
      if (constants != null) {
        return Arrays.stream(constants).map(e -> ((Enum<?>) e).name()).collect(Collectors.toList());
      }
    }
    return Collections.emptyList();
  }

  private static String mapJavaTypeToJsonType(final Class<?> type) {
    if (Number.class.isAssignableFrom(type) || type.isPrimitive() && !type.equals(boolean.class)) {
      return "number";
    }
    if (type.equals(String.class)) {
      return "string";
    }
    if (type.equals(Boolean.class) || type.equals(boolean.class)) {
      return "boolean";
    }
    if (type.isArray() || java.util.Collection.class.isAssignableFrom(type)) {
      return "array";
    }
    // Fallback, even for enums
    return "string";
  }
}
