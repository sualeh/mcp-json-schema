package us.fatehi.model_json_schema;

import org.junit.jupiter.api.Test;

public class JsonSchemaTest {

  @Test
  public void testSimpleParameters() throws Exception {
    final String schemaJson =
        JsonSchemaGenerator.generateSchema(SampleParameters.class).toPrettyString();
    System.out.println(schemaJson);
  }
}
