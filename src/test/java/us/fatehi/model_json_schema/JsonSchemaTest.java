package us.fatehi.model_json_schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class JsonSchemaTest {

  @Test
  public void testSimpleParameters() throws Exception {
    final String schemaJson =
        JsonSchemaGenerator.generateSchema(SampleParameters.class).toPrettyString();

    final String expectedJson = new String(
        getClass().getResourceAsStream("/expected-schema-simple-parameters.json").readAllBytes(),
        StandardCharsets.UTF_8);

    assertEquals(expectedJson.strip(), schemaJson.strip());
  }
}
