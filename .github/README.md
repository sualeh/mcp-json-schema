**:star: Star it :arrow_heading_up: if you love it!**

[![Quick Build](https://github.com/sualeh/mcp-json-schema/actions/workflows/quick-build.yml/badge.svg)](https://github.com/sualeh/mcp-json-schema/actions/workflows/quick-build.yml)
[![The Central Repository](https://img.shields.io/maven-central/v/us.fatehi/mcp-json-schema.svg)](https://search.maven.org/search?q=g:us.fatehi%20mcp-json-schema*)


# MCP JSON Schema

Generates JSON Schema for MCP servers from a Jackson 3 annotated Java class.

This library targets the Model Context Protocol (MCP) JSON Schema, which is a subset of the general JSON Schema specification. For details on the MCP JSON Schema subset and primitive types, see the MCP [PrimitiveSchemaDefinition](https://modelcontextprotocol.io/specification/2025-06-18/schema#primitiveschemadefinition).

> See [Generate MCP Tool Schemas Directly From Java Code](https://dev.to/sualeh/generate-mcp-tool-schemas-directly-from-java-code-3bif)

## Usage

### Add a Dependency

Add a dependency to [mcp-json-schema](https://central.sonatype.com/artifact/us.fatehi/mcp-json-schema) in Maven or Gradle.


### Define Your MCP Tool Parameters Type

Create a Jackson‑annotated record or Java class that represents the tool's input parameters. Use `@JsonProperty(required=true, defaultValue=...)` and `@JsonPropertyDescription` to enrich the schema. Use Jakarta validations like `@Max`,`@Min`, `@Positive`, `@PositiveOrZero`, `@Negative`, `@NegativeOrZero` on numbers, or `@Size`, `@NotBlank` on strings.

Here is an example:

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

### Generate the MCP `inputSchema` for Tool Registration

When registering your MCP tool, use `McpJsonSchemaUtility.inputSchema(...)` to generate the `inputSchema` JSON for the parameters type.

```java
import us.fatehi.mcp_json_schema.McpJsonSchemaUtility;

// Provide this value as the tool's input_schema in your MCP server implementation
String inputSchemaJson = McpJsonSchemaUtility.inputSchema(SampleParameters.class);
```

You can also obtain the schema as a Jackson `JsonNode` if you prefer to embed or modify it programmatically:

```java
var schemaNode = McpJsonSchemaUtility.generateJsonSchema(SampleParameters.class);
```

### Instantiate Arguments for Execution

When the tool is invoked by a client, you'll receive an `arguments` JSON string. Convert it into your parameters type using `instantiateArguments(...)`.

```java
import tools.jackson.databind.JsonNode;
import us.fatehi.mcp_json_schema.DeserializationUtility;

String arguments = """
{
  "dependant-object-type": "COLUMNS",
  "table-name": "customers"
}
""";

SampleParameters argsObject = 
  DeserializationUtility.instantiateArguments(arguments, SampleParameters.class);
```
