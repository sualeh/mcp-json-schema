/*
 * MCP JSON Schema
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.mcp_json_schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyMetadata;
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
    final List<BeanPropertyDefinition> propertyDefinitions = beanDesc.findProperties();
    for (final BeanPropertyDefinition propertyDefinition : propertyDefinitions) {
      System.out.println(propertyDefinition.getGetter().getFullName());
      setProperty(propertiesNode, propertyDefinition);
      addRequired(propertyDefinition, required);
    }

    final ArrayNode requiredArray = schemaNode.putArray("required");
    required.forEach(requiredArray::add);

    schemaNode.put("additionalProperties", false);

    return schemaNode;
  }

  private static void addRequired(
      final BeanPropertyDefinition propertyDefinition, final List<String> required) {
    final AnnotatedMember accessor = propertyDefinition.getAccessor();
    if (accessor.hasAnnotation(JsonProperty.class)) {
      final JsonProperty jsonProperty = accessor.getAnnotation(JsonProperty.class);
      if (jsonProperty.required()) {
        final String propertyName = propertyDefinition.getName();
        required.add(propertyName);
      }
    }
  }

  private static void setEnumValues(final ObjectNode node, final JavaType javaType) {
    if (javaType.isEnumType()) {
      final ArrayNode enumValuesNode = node.putArray("enum");
      final Object[] constants = javaType.getRawClass().getEnumConstants();
      for (final Object e : constants) {
        enumValuesNode.add(((Enum<?>) e).name());
      }
    }
  }

  private static void setProperty(
      final ObjectNode propertiesNode, final BeanPropertyDefinition propertyDefinition) {

    final JavaType javaType = propertyDefinition.getPrimaryType();
    final PropertyMetadata propertyMetadata = propertyDefinition.getMetadata();

    final ObjectNode parameterNode = propertiesNode.putObject(propertyDefinition.getName());

    setType(parameterNode, javaType);

    final String description = propertyMetadata.getDescription();
    if (description != null && !description.strip().isBlank()) {
      parameterNode.put("description", description.replaceAll("\\R", " ").strip());
    }

    setEnumValues(parameterNode, javaType);
  }

  private static void setType(final ObjectNode node, final JavaType javaType) {
    final Class<?> type = javaType.getRawClass();

    final String typeName;
    if (Number.class.isAssignableFrom(type) || type.isPrimitive() && !type.equals(boolean.class)) {
      typeName = "number";
    } else if (type.equals(String.class)) {
      typeName = "string";
    } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
      typeName = "boolean";
    } else if (javaType.isArrayType() || javaType.isCollectionLikeType()) {
      typeName = "array";
    } else {
      typeName = "string";
    }

    node.put("type", typeName);
  }
}
