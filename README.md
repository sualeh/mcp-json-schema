# MCP JSON Schema

Generates JSON Schema for MCP servers from a Jackson‑annotated Java class. Uses Java 17 and Jackson 3.

This library targets the Model Context Protocol (MCP) JSON Schema, which is a subset of the general JSON Schema specification. For details on the MCP JSON Schema subset and primitive types, see the MCP [PrimitiveSchemaDefinition](https://modelcontextprotocol.io/specification/2025-06-18/schema#primitiveschemadefinition).

## Usage

### 1) Define your parameters type

Create a Jackson‑annotated record/POJO that represents the tool's input parameters. Use `@JsonProperty(required=true, defaultValue=...)` and `@JsonPropertyDescription` to enrich the schema.

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

### 2) Generate the MCP input_schema at registration time

When registering your MCP tool, use `McpJsonSchemaUtility.inputSchema(...)` to generate the `input_schema` JSON for the parameters type.

```
import us.fatehi.mcp_json_schema.McpJsonSchemaUtility;

String inputSchemaJson = McpJsonSchemaUtility.inputSchema(SampleParameters.class);

// Provide this value as the tool's input_schema in your MCP server implementation
System.out.println(inputSchemaJson);
```

You can also obtain the schema as a Jackson `JsonNode` if you prefer to embed or modify it programmatically:

```
var schemaNode = McpJsonSchemaUtility.generateJsonSchema(SampleParameters.class);
```

### 3) Instantiate arguments at execution time

When the tool is invoked by a client, you'll receive an `arguments` JSON string. Convert it into your parameters type using `instantiateArguments(...)`.

```
String arguments = "{\n  \"dependant-object-type\": \"COLUMNS\",\n  \"table-name\": \"customers\"\n}";

SampleParameters params = McpJsonSchemaUtility.instantiateArguments(arguments, SampleParameters.class);
if (params == null) {
  // handle error: bad arguments; return a suitable MCP error to the client
} else {
  // execute tool logic using params
}
```

### Notes

- The generated schema adheres to the MCP JSON Schema subset (see the spec link above). It includes:
  - `type: "object"`, `properties`, and `required` (from `@JsonProperty(required = true)`).
  - `description` (from `@JsonPropertyDescription`).
  - `enum` values for enums (including array items where applicable).
  - `additionalProperties: false`.
- Naming strategies such as `@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)` influence property names in both the schema and deserialization.
- `instantiateArguments` uses a shared `ObjectMapper` with default configuration. On deserialization failure, it returns `null` and logs at INFO. Make sure to validate and handle `null` appropriately.

### See also

- `us.fatehi.mcp_json_schema.McpJsonSchemaUtility` Javadoc for API details and usage guidance.
