package ovh.miroslaw.kindle2anki;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import ovh.miroslaw.kindle2anki.model.Root;

import java.util.List;

@Command
@RequiredArgsConstructor
public class Commands {

    private final DictionaryProvider mwClient;

    @Command(alias = "t")
    public String test() {
        String json = mwClient.getDefinition("matrix");
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Root>> typeReference = new TypeReference<>() {};
        try {
//            List<Root> root = mapper.readValue(json, typeReference);
            List<Root> root = mapper.readValue(json, typeReference);
            System.out.println(root);
            JsonNode defnitions = mapper.readValue(json, JsonNode.class);


        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }
}
