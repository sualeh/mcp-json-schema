/*
 * MCP JSON Schema
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.mcp_json_schema;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.List;

public class JsonSchemaGenerator {

  private static final ObjectMapper mapper = new ObjectMapper();

  public static JsonNode generateSchema(Class<?> clazz) throws Exception {

    SerializationConfig config = mapper.getSerializationConfig();
    BeanDescription beanDesc =
        config.introspect(TypeFactory.defaultInstance().constructType(clazz));

    final ObjectNode schemaNode = mapper.createObjectNode();

    schemaNode.put("type", "object");
    final ObjectNode propertiesNode = schemaNode.putObject("properties");
    List<BeanPropertyDefinition> properties = beanDesc.findProperties();
    for (BeanPropertyDefinition prop : properties) {
      System.out.println(prop.getGetter().getFullName());
      final ObjectNode parameterSchema = propertiesNode.putObject(prop.getName());
      String typeName = mapJavaTypeToJsonType(prop.getPrimaryType().getRawClass());
      parameterSchema.put("type", typeName);
    }

    return schemaNode;
  }

  // Example usage
  public static void main(String[] args) throws Exception {
    String schemaJson = generateSchema(SampleClass.class).toPrettyString();
    System.out.println(schemaJson);
  }

  private static String mapJavaTypeToJsonType(Class<?> type) {
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
    if (type.isEnum() || !type.getPackageName().startsWith("java.")) {
      return "object"; // treat enums and custom classes as objects
    }
    return "string"; // fallback
  }
}
