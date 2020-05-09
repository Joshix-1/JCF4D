package CommandHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;
import util.JCF4DUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class CommandHandlerPrefixes extends CommandHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    protected JsonNode prefixes;


    protected CommandHandlerPrefixes(DiscordApi api, String defaultPrefix, JsonNode prefixes) {
        super(api, defaultPrefix);
        this.prefixes = prefixes;
    }

    protected CommandHandlerPrefixes(DiscordApi api, String defaultPrefix, File jsonFile) throws IOException {
        this(api, defaultPrefix, JCF4DUtils.readTextFromFile(jsonFile));
    }

    protected CommandHandlerPrefixes(DiscordApi api, String defaultPrefix, String jsonString) throws IOException {
        super(api, defaultPrefix);

        prefixes = objectMapper.readTree(jsonString);
    }

    @Override
    public List<String> getPrefixes(MessageCreateEvent event) {
        return super.getPrefixes(event);
    }

    protected List<String> getPrefixes(String key) {
        if(!this.prefixes.has(key)) {
            return super.getPrefixes();
        }

        String prefix = this.prefixes.get(key).asText();

        if(prefix.isEmpty()) {
            return super.getPrefixes();
        }

        List<String> prefixes = new ArrayList<>(3);

        prefixes.add(prefix);
        super.addMentionPrefixes(prefixes);

        return prefixes;
    }
}
