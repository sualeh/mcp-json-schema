package us.fatehi.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import us.fatehi.mcp_json_schema.DeserializationUtility;
import us.fatehi.mcp_json_schema.McpJsonSchemaUtility;

public class AllTypesParametersTest {

  @Test
  public void testInputSchema_AllTypesParameters() throws Exception {
    final String schemaJson = McpJsonSchemaUtility.inputSchema(AllTypesParameters.class);

    final String expectedJson =
        new String(
            getClass()
                .getResourceAsStream("/expected-schema-all-types-parameters.json")
                .readAllBytes(),
            StandardCharsets.UTF_8);

    final ObjectMapper mapper = new ObjectMapper();
    final JsonNode expected = mapper.readTree(expectedJson);
    final JsonNode actual = mapper.readTree(schemaJson);

    assertEquals(expected, actual);
  }

  @Test
  public void testInstantiateArguments_AllTypesParameters_fromJson() {
    final String args =
        """
        {
          "integerValue": 42,
          "numberValue": 3.1415,
          "stringValue": "hello",
          "booleanValue": true,
          "enumValue": "B",
          "integerArray": [1, 2, 3],
          "enumArray": ["A", "C"]
        }
        """;

    final AllTypesParameters params =
        DeserializationUtility.instantiateArguments(args, AllTypesParameters.class);

    assertNotNull(params);
    assertEquals(42, params.integerValue());
    assertEquals(3.1415, params.numberValue(), 0.0000001);
    assertEquals("hello", params.stringValue());
    assertEquals(true, params.booleanValue());
    assertEquals(AllTypesParameters.MyEnum.B, params.enumValue());
    assertArrayEquals(new int[] {1, 2, 3}, params.integerArray());
    assertArrayEquals(
        new AllTypesParameters.MyEnum[] {AllTypesParameters.MyEnum.A, AllTypesParameters.MyEnum.C},
        params.enumArray());
  }
}
