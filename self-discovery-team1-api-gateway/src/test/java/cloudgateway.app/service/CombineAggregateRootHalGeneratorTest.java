package cloudgateway.app.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CombineAggregateRootHalGenerator.class)
public class CombineAggregateRootHalGeneratorTest {

   private static final String STUDENT_ROOT_HAL_JSON_OUTPUT = "{\n" +
         "    \"_links\": {\n" +
         "        \"students\": {\n" +
         "            \"href\": \"http://localhost:8888/students{?page,size,sort}\",\n" +
         "            \"templated\": true\n" +
         "        },\n" +
         "        \"profile\": {\n" +
         "            \"href\": \"http://localhost:8888/profile\"\n" +
         "        }\n" +
         "    }\n" +
         "}";

   private static final String VALID_STUDENT_ROOT_HAL_JSON_OUTPUT_RESULT = "{\"_links\":{\"students\":{\"href\":\"http://localhost:8888/students{?page,size,sort}\",\"templated\":true},\"profile\":{\"href\":\"http://localhost:8888/profile\"}}}";


   private static final String LEARNING_MS_ROOT_HAL_JSON_OUTPUT = "{\n" +
         "    \"_links\": {\n" +
         "        \"quizzes\": {\n" +
         "            \"href\": \"http://localhost:8888/quizzes{?page,size,sort}\",\n" +
         "            \"templated\": true\n" +
         "        },\n" +
         "        \"questions\": {\n" +
         "            \"href\": \"http://localhost:8888/questions{?page,size,sort,projection}\",\n" +
         "            \"templated\": true\n" +
         "        },\n" +
         "        \"profile\": {\n" +
         "            \"href\": \"http://localhost:8888/profile\"\n" +
         "        }\n" +
         "    }\n" +
         "}";

   private static final String VALID_STUDENT_AND_LEARNING_MS_ROOT_HAL_JSON_OUTPUT = "{\"_links\":{\"students\":{\"href\":\"http://localhost:8888/students{?page,size,sort}\",\"templated\":true},\"profile\":{\"href\":\"http://localhost:8888/profile\"},\"quizzes\":{\"href\":\"http://localhost:8888/quizzes{?page,size,sort}\",\"templated\":true},\"questions\":{\"href\":\"http://localhost:8888/questions{?page,size,sort,projection}\",\"templated\":true},\"profile\":{\"href\":\"http://localhost:8888/profile\"}}}";

   @Autowired
   private HalGenerator generator;

   @Test
   public void generateValidJsonWithEmptyLinksListOnEmptySet() {

      String generatedJson = generator.getHalString(Arrays.asList());

      assertThat(generatedJson).isEqualTo("{\"_links\":{}}");
   }

   @Test
   public void generatedValidJsonWithEmptyLinksListForOneEmptyJsonInSet() {
      String validEmptyLinksJson = "{\"_links\":{}}";
      List<String> json = Arrays.asList(validEmptyLinksJson);

      String generatedJson = generator.getHalString(json);

      assertThat(generatedJson).isEqualTo("{\"_links\":{}}");
   }

   @Test
   public void generatedValidJsonWithEmptyLinksListForTwoEmptyJsonInSet() {
      String validEmptyLinksJson = "{\"_links\":{}}";

      List<String> json = Arrays.asList(validEmptyLinksJson, new String(validEmptyLinksJson));

      String generatedJson = generator.getHalString(json);

      assertThat(generatedJson).isEqualTo("{\"_links\":{}}");
   }


   @Test
   public void generateEmptyJsonOnOneInvalidJsonInSet() {

      String invalidJson = "{\"_links\": { \"students: {";
      List<String> json = Arrays.asList(invalidJson);

      String generatedJson = generator.getHalString(json);

      assertThat(generatedJson).isEqualTo("{}");
   }

   @Test
   public void generateValidLinkListWithOneValidHalJsonInSet() {

      List<String> json = Arrays.asList(STUDENT_ROOT_HAL_JSON_OUTPUT);

      String generatedJson = generator.getHalString(json);

      assertThat(generatedJson).isEqualTo(VALID_STUDENT_ROOT_HAL_JSON_OUTPUT_RESULT);
   }

   @Test
   public void generateValidLinkListWithTwoValidHalJsonInSet() {
      List<String> json = Arrays.asList(STUDENT_ROOT_HAL_JSON_OUTPUT, LEARNING_MS_ROOT_HAL_JSON_OUTPUT);

      String generatedJson = generator.getHalString(json);

      assertThat(generatedJson).isEqualToIgnoringNewLines(VALID_STUDENT_AND_LEARNING_MS_ROOT_HAL_JSON_OUTPUT);
   }

   @Test
   public void generatedValidJsonWithOneEmptyLinksListAndStudentLinksListJsonsInSet() {
      List<String> json = Arrays.asList(STUDENT_ROOT_HAL_JSON_OUTPUT, "{\"_links\":{}}");

      String generatedJson = generator.getHalString(json);

      assertThat(generatedJson).isEqualTo(VALID_STUDENT_ROOT_HAL_JSON_OUTPUT_RESULT);
   }
}
