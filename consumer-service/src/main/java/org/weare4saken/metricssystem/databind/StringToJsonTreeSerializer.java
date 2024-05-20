package org.weare4saken.metricssystem.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StringToJsonTreeSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        try (JsonParser parser = jsonGenerator.getCodec().getFactory().createParser(s)) {
            TreeNode tree = parser.readValueAsTree();
            jsonGenerator.writeTree(tree);
        } catch (JsonProcessingException e) {
            jsonGenerator.writeString(s);
        }
    }
}
