package cloudgateway.app.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CombineAggregateRootHalGenerator implements HalGenerator {

   @Override
   public String getHalString(List<String> jsonList) {
      JsonFactory jsonFactory = new JsonFactory();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ObjectMapper mapper = new ObjectMapper();
      String jsonString = "{}";
      try {
         JsonGenerator generator = jsonFactory.createGenerator(outputStream);

         generator.setCodec(mapper);
         generator.writeStartObject();
         generator.writeFieldName("_links");
         generator.writeStartObject();

         jsonList.stream()
               .map(json -> {
                  return getLinkJsonNode(json, mapper);
               })
               .forEach(jsonNode -> {
                  jsonNode.fields().forEachRemaining(field -> {
                     addFieldToGenerator(field, generator);
                  });
               });

         generator.writeEndObject();
         generator.writeEndObject();

         generator.close();

         jsonString = outputStream.toString();
         outputStream.close();
      }
      catch (Exception e) {
         e.printStackTrace();
      }

      return jsonString;
   }

   private void addFieldToGenerator(Map.Entry<String, JsonNode> field, JsonGenerator generator) {
      try {
         generator.writeFieldName(field.getKey());
         generator.writeTree(field.getValue());
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private JsonNode getLinkJsonNode(String jsonString, ObjectMapper mapper) {
      try {
         JsonNode actualObj = mapper.readTree(jsonString);
         return actualObj.get("_links");
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}
