package us.fatehi.mcp_json_schema;

public class Main {

  // Example usage
  public static void main(final String[] args) throws Exception {
    final String schemaJson =
        JsonSchemaGenerator.generateSchema(SampleClass.class).toPrettyString();
    System.out.println(schemaJson);
  }
}
