---
title: Generate MCP Tool Schemas Directly From Java Code
published: false
description: Stop maintaining MCP JSON Schemas by hand. Point the utility at your Jackson 3 annotated class and ship.
tags: java, mcp, json, jsonschema
---

If you are building an MCP server, every tool you expose needs an `inputSchema`. MCP servers written with [Spring AI](https://spring.io/projects/spring-ai) support often start with a simple data class for tool inputs. Then come changes: a new field, a renamed property, or updated constraints. The JSON schema in the tool registration rarely keeps up - that means clients may send invalid payloads. By generating the schema from the source of truth — the Java type — you remove that drift.

Writing that JSON by hand is repetitive, easy to get wrong. [MCP supports only a specific sub-type](https://modelcontextprotocol.io/specification/2025-06-18/schema#primitiveschemadefinition) of the [JSON Schema specification](https://json-schema.org/specification). The [MCP JSON Schema](https://github.com/sualeh/mcp-json-schema) library keeps the parameter schema and the code in lockstep by generating the MCP-compatible JSON Schema from a Jackson 3 annotated Java class or record.

What you get:
- Use of Jackson 3 annotations for naming, required fields, and descriptions that carry  over into the schema
- Use of Jakarta Bean Validation to adds meaningful constraints to the schema (for example, `@Max`,`@Min`, `@Positive`, `@PositiveOrZero`, `@Negative`, `@NegativeOrZero` on numbers, or `@Size`, `@NotBlank` on strings)
- Automatic handling of required fields, defaults, enums, and descriptions
- Output that targets the MCP JSON Schema subset, not the entire JSON Schema specification


### How it works

Add a dependency to us.fatehi:mcp-json-schema in Maven or Gradle.

```xml
<dependency>
    <groupId>us.fatehi</groupId>
    <artifactId>mcp-json-schema</artifactId>
    <version>1.0.1</version>
</dependency>
```

Define a parameters type as a Jackson‑annotated record or class and let the library produce the `inputSchema` JSON. Use annotations to describe intent, and let the library translate that into the MCP schema format.

For example:

```java
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record SampleParameters(
    @JsonPropertyDescription("Type of database table dependant objects.")
    @JsonProperty(defaultValue = "NONE", required = true)
    DependantObjectType dependantObjectType,

    @JsonPropertyDescription("Table name.")
    String tableName) {

  public enum DependantObjectType { NONE, COLUMNS, INDEXES, FOREIGN_KEYS, TRIGGERS }
}
```

Next, generate the MCP `inputSchema`:

```java
import us.fatehi.mcp_json_schema.McpJsonSchemaUtility;

// Provide this value as the tool's input_schema in your MCP server implementation
String inputSchemaJson = McpJsonSchemaUtility.inputSchema(SampleParameters.class);
```

Prefer a `JsonNode` for programmatic changes? Use:

```java
var schemaNode = McpJsonSchemaUtility.generateJsonSchema(SampleParameters.class);
```

----------

The source code is available at [sualeh/mcp-json-schema](https://github.com/sualeh/mcp-json-schema)
