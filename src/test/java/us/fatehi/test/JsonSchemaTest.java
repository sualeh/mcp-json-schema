package us.fatehi.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import us.fatehi.mcp_json_schema.McpJsonSchemaUtility;

public class JsonSchemaTest {

  @Test
  public void testInputSchemaSimpleParameters() throws Exception {
    // Get schema via inputSchema (compact string)
    final String schemaJson = McpJsonSchemaUtility.inputSchema(SampleParameters.class);

    // Load expected (pretty-printed) JSON
    final String expectedJson =
        new String(
            getClass()
                .getResourceAsStream("/expected-schema-simple-parameters.json")
                .readAllBytes(),
            StandardCharsets.UTF_8);

    // Compare as JSON structures so formatting/ whitespace differences don't matter
    final ObjectMapper mapper = new ObjectMapper();
    final JsonNode expected = mapper.readTree(expectedJson);
    final JsonNode actual = mapper.readTree(schemaJson);

    assertEquals(expected, actual);
  }

  @Test
  public void testInstantiateArguments_blankString() {
    final SampleParameters params =
        McpJsonSchemaUtility.instantiateArguments("", SampleParameters.class);

    // For blank input, we expect an object instantiated from an empty JSON object.
    // Fields not provided should be null unless defaults are applied by the mapper.
    assertNull(params);
  }

  @Test
  public void testInstantiateArguments_malformedJson() {
    final SampleParameters params =
        McpJsonSchemaUtility.instantiateArguments("{", SampleParameters.class);

    // Malformed JSON should be handled gracefully and return null
    assertNull(params);
  }

  @Test
  public void testInstantiateArguments_null() {
    final SampleParameters params =
        McpJsonSchemaUtility.instantiateArguments(null, SampleParameters.class);

    // For blank input, we expect an object instantiated from an empty JSON object.
    // Fields not provided should be null unless defaults are applied by the mapper.
    assertNull(params);
  }

  @Test
  public void testInstantiateArguments_validJson() {
    final String args =
        """
        {
          "dependant-object-type": "COLUMNS",
          "table-name": "my_table"
        }
        """;

    final SampleParameters params =
        McpJsonSchemaUtility.instantiateArguments(args, SampleParameters.class);

    assertNotNull(params);
    assertEquals(SampleParameters.DependantObjectType.COLUMNS, params.dependantObjectType());
    assertEquals("my_table", params.tableName());
  }

  @Test
  public void testSimpleParameters() throws Exception {
    final String schemaJson =
        McpJsonSchemaUtility.generateJsonSchema(SampleParameters.class).toPrettyString();

    final String expectedJson =
        new String(
            getClass()
                .getResourceAsStream("/expected-schema-simple-parameters.json")
                .readAllBytes(),
            StandardCharsets.UTF_8);

    // Compare as JSON structures so formatting/ whitespace differences don't matter
    final ObjectMapper mapper = new ObjectMapper();
    final JsonNode expected = mapper.readTree(expectedJson);
    final JsonNode actual = mapper.readTree(schemaJson);

    assertEquals(expected, actual);
  }
}
