package us.fatehi.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import us.fatehi.mcp_json_schema.utility.McpJsonSchemaUtility;

public class StringLengthParametersTest {

  @Test
  public void testInputSchema_StringLengthParameters() throws Exception {
    final String schemaJson = McpJsonSchemaUtility.inputSchema(StringLengthParameters.class);

    final String expectedJson =
        new String(
            getClass()
                .getResourceAsStream("/expected-schema-string-length-parameters.json")
                .readAllBytes(),
            StandardCharsets.UTF_8);

    final ObjectMapper mapper = new ObjectMapper();
    final JsonNode expected = mapper.readTree(expectedJson);
    final JsonNode actual = mapper.readTree(schemaJson);

    assertEquals(expected, actual);
  }
}
