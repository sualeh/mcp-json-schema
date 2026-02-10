package us.fatehi.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import us.fatehi.mcp_json_schema.utility.DeserializationUtility;

public class DeserializationUtilityTest {

  private static final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void testDeserialize_null_returnsEmptyObject() {
    final JsonNode node = DeserializationUtility.toJsonObject(null);
    assertNotNull(node, "Should never return null");
    assertTrue(node.isObject());
    assertEquals(mapper.createObjectNode(), node, "Null input should yield empty object");
  }

  @Test
  public void testDeserialize_blank_returnsEmptyObject() {
    final JsonNode node = DeserializationUtility.toJsonObject("   \n\t");
    assertNotNull(node);
    assertTrue(node.isObject());
    assertEquals(mapper.createObjectNode(), node, "Blank input should yield empty object");
  }

  @Test
  public void testDeserialize_validObject_returnsObject() {
    final String json = "{\n  \"name\": \"alpha\",\n  \"value\": 42\n}";
    final JsonNode node = DeserializationUtility.toJsonObject(json);
    assertNotNull(node);
    assertTrue(node.isObject());
    assertEquals("alpha", node.path("name").asString());
    assertEquals(42, node.path("value").asInt());
  }

  @Test
  public void testDeserialize_array_returnsEmptyObject() {
    final JsonNode node = DeserializationUtility.toJsonObject("[1,2,3]");
    assertNotNull(node);
    assertTrue(node.isObject());
    assertEquals(0, node.size(), "Array input should yield empty object");
  }

  @Test
  public void testDeserialize_primitive_returnsEmptyObject() {
    final JsonNode node = DeserializationUtility.toJsonObject("123");
    assertNotNull(node);
    assertTrue(node.isObject());
    assertEquals(0, node.size(), "Primitive input should yield empty object");
  }

  @Test
  public void testDeserialize_malformed_returnsEmptyObject() {
    final JsonNode node = DeserializationUtility.toJsonObject("{");
    assertNotNull(node);
    assertTrue(node.isObject());
    assertEquals(0, node.size(), "Malformed input should yield empty object");
  }
}
