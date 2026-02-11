/*
 * MCP JSON Schema
 * Copyright (c) 2025-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package us.fatehi.mcp_json_schema.generator;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyMetadata;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.BasicClassIntrospector;
import tools.jackson.databind.introspect.BeanPropertyDefinition;
import tools.jackson.databind.introspect.ClassIntrospector;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

public final class McpJsonSchemaGenerator {

  private static final Logger LOGGER =
      Logger.getLogger(McpJsonSchemaGenerator.class.getCanonicalName());

  private final ObjectMapper mapper;

  public McpJsonSchemaGenerator(final ObjectMapper mapper) {
    this.mapper = Objects.requireNonNull(mapper, "Object mapper must not be null");
  }

  public JsonNode generateJsonSchema(final Class<?> clazz) {

    Objects.requireNonNull(clazz, "Class must not be null");

    final BeanDescription beanDesc = createBeanDescription(clazz);

    final ObjectNode schemaNode = mapper.createObjectNode();

    schemaNode.put("type", "object");
    final ObjectNode propertiesNode = schemaNode.putObject("properties");
    final List<String> required = new ArrayList<>();
    final List<BeanPropertyDefinition> propertyDefinitions = beanDesc.findProperties();
    for (final BeanPropertyDefinition propertyDefinition : propertyDefinitions) {
      LOGGER.log(Level.FINE, propertyDefinition.getGetter().getFullName());

      final String propertyName = propertyDefinition.getName();
      final JavaType javaType = propertyDefinition.getPrimaryType();
      final PropertyMetadata propertyMetadata = propertyDefinition.getMetadata();

      final ObjectNode parameterNode = propertiesNode.putObject(propertyName);
      setType(parameterNode, javaType);

      final String description = propertyMetadata.getDescription();
      if (description != null && !description.strip().isBlank()) {
        parameterNode.put("description", description);
      }

      setEnumValues(parameterNode, javaType);
      setItems(parameterNode, javaType);
      setMinMaxValues(parameterNode, propertyDefinition);

      if (propertyMetadata.isRequired()) {
        required.add(propertyName);
      }
    }

    final ArrayNode requiredArray = schemaNode.putArray("required");
    required.forEach(requiredArray::add);

    schemaNode.put("additionalProperties", false);

    return schemaNode;
  }

  private BeanDescription createBeanDescription(final Class<?> clazz) {

    final SerializationConfig config = mapper.serializationConfig();
    final ClassIntrospector introspector = new BasicClassIntrospector().forOperation(config);

    final JavaType type = mapper.getTypeFactory().constructType(clazz);
    final AnnotatedClass classDef = introspector.introspectClassAnnotations(type);
    final BeanDescription beanDesc = introspector.introspectForSerialization(type, classDef);

    return beanDesc;
  }

  private Long[] findNumberRange(final BeanPropertyDefinition propertyDefinition) {

    final AnnotatedMember accessor = propertyDefinition.getAccessor();

    final Min minAnnotation = accessor.getAnnotation(Min.class);
    final Max maxAnnotation = accessor.getAnnotation(Max.class);
    final Positive posAnnotation = accessor.getAnnotation(Positive.class);
    final PositiveOrZero pos0Annotation = accessor.getAnnotation(PositiveOrZero.class);
    final Negative negAnnotation = accessor.getAnnotation(Negative.class);
    final NegativeOrZero neg0Annotation = accessor.getAnnotation(NegativeOrZero.class);

    Long min = null;
    if (posAnnotation != null) {
      min = 1L;
    }
    if (pos0Annotation != null) {
      min = 0L;
    }
    if (minAnnotation != null) {
      min = minAnnotation.value();
    }

    Long max = null;
    if (maxAnnotation != null) {
      max = maxAnnotation.value();
    }
    if (neg0Annotation != null) {
      max = 0L;
    }
    if (negAnnotation != null) {
      max = -1L;
    }

    return new Long[] {min, max};
  }

  private Long[] findStringLengthRange(final BeanPropertyDefinition propertyDefinition) {
    final AnnotatedMember accessor = propertyDefinition.getAccessor();

    final Size sizeAnnotation = accessor.getAnnotation(Size.class);
    final NotBlank notBlankAnnotation = accessor.getAnnotation(NotBlank.class);

    Long min = null;
    if (notBlankAnnotation != null) {
      min = 1L;
    }
    if (sizeAnnotation != null) {
      min = (long) sizeAnnotation.min();
    }
    if (min != null && min == 0L) {
      min = null;
    }
    Long max = null;
    if (sizeAnnotation != null) {
      max = (long) sizeAnnotation.max();
    }
    if (max != null && max == Integer.MAX_VALUE) {
      max = null;
    }

    return new Long[] {min, max};
  }

  private void setEnumValues(final ObjectNode node, final JavaType javaType) {
    if (javaType.isEnumType()) {
      final ArrayNode enumValuesNode = node.putArray("enum");
      final Object[] constants = javaType.getRawClass().getEnumConstants();
      for (final Object e : constants) {
        enumValuesNode.add(((Enum<?>) e).name());
      }
    }
  }

  private void setFormat(final ObjectNode node, final JavaType javaType) {
    final Class<?> type = javaType.getRawClass();
    try {
      if (URI.class.equals(type)) {
        node.put("format", "uri");
        return;
      }

      if (LocalDate.class.equals(type)) {
        node.put("format", "date");
        return;
      }

      if (List.of(LocalDateTime.class, OffsetDateTime.class, ZonedDateTime.class).contains(type)) {
        node.put("format", "date-time");
      }
    } catch (final Exception e) {
      LOGGER.log(Level.FINE, e.getMessage(), e);
    }
  }

  private void setItems(final ObjectNode node, final JavaType javaType) {
    if (javaType.isArrayType() || javaType.isCollectionLikeType()) {
      final ObjectNode itemsNode = node.putObject("items");
      final JavaType contentType = javaType.getContentType();
      setType(itemsNode, contentType);
      setFormat(itemsNode, contentType);
      setEnumValues(itemsNode, contentType);
    }
  }

  private void setMinMaxValues(
      final ObjectNode node, final BeanPropertyDefinition propertyDefinition) {
    if (String.class.equals(propertyDefinition.getRawPrimaryType())) {
      final Long[] stringLengthRange = findStringLengthRange(propertyDefinition);
      final Long minLength = stringLengthRange[0];
      if (minLength != null) {
        node.put("minLength", String.valueOf(minLength));
      }
      final Long maxLength = stringLengthRange[1];
      if (maxLength != null) {
        node.put("maxLength", String.valueOf(maxLength));
      }
      return;
    }

    final Long[] numberRange = findNumberRange(propertyDefinition);
    final Long minimum = numberRange[0];
    if (minimum != null) {
      node.put("minimum", String.valueOf(minimum));
    }
    final Long maximum = numberRange[1];
    if (maximum != null) {
      node.put("maximum", String.valueOf(maximum));
    }
  }

  private void setType(final ObjectNode node, final JavaType javaType) {
    final Class<?> type = javaType.getRawClass();

    final String typeName;
    if (List.of(short.class, int.class, long.class, Short.class, Integer.class, Long.class)
        .contains(type)) {
      // Integers are short, int, long (primitive and boxed)
      typeName = "integer";
    } else if (Number.class.isAssignableFrom(type)
        || List.of(byte.class, float.class, double.class).contains(type)) {
      // All other numbers remain as "number"
      typeName = "number";
    } else if (type.equals(String.class)) {
      typeName = "string";
    } else if (List.of(Boolean.class, boolean.class).contains(type)) {
      typeName = "boolean";
    } else if (javaType.isArrayType() || javaType.isCollectionLikeType()) {
      typeName = "array";
    } else {
      typeName = "string";
    }

    node.put("type", typeName);
    if (Objects.equals(typeName, "string")) {
      setFormat(node, javaType);
    }
  }
}
