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
import java.util.List;

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
    final List<BeanPropertyDefinition> properties = beanDesc.findProperties();
    for (final BeanPropertyDefinition prop : properties) {
      System.out.println(prop.getGetter().getFullName());
      final String propertyName = prop.getName();
      final ObjectNode parameterSchema = propertiesNode.putObject(propertyName);

      final String typeName = mapJavaTypeToJsonType(prop.getPrimaryType().getRawClass());
      parameterSchema.put("type", typeName);

      final AnnotatedMember accessor = prop.getAccessor();

      if (accessor.hasAnnotation(JsonPropertyDescription.class)) {
        final JsonPropertyDescription description =
            accessor.getAnnotation(JsonPropertyDescription.class);
        parameterSchema.put("description", description.value());
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

    return schemaNode;
  }

  // Example usage
  public static void main(final String[] args) throws Exception {
    final String schemaJson = generateSchema(SampleClass.class).toPrettyString();
    System.out.println(schemaJson);
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
    if (type.isEnum()) {}
    return "string"; // fallback
  }
}
