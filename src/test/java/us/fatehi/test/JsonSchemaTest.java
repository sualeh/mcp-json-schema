package us.fatehi.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import us.fatehi.mcp_json_schema.McpJsonSchemaUtility;

public class JsonSchemaTest {

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
}
