package CommandHandler;

import com.fasterxml.jackson.databind.JsonNode;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CommandHandlerUserPrefixes extends CommandHandlerPrefixes {

    protected CommandHandlerUserPrefixes(DiscordApi api, String defaultPrefix, JsonNode prefixes) {
        super(api, defaultPrefix, prefixes);
    }

    public CommandHandlerUserPrefixes(DiscordApi api, String defaultPrefix, File file) throws IOException {
        super(api, defaultPrefix, file);
    }

    protected CommandHandlerUserPrefixes(DiscordApi api, String defaultPrefix, String jsonString) throws IOException {
        super(api, defaultPrefix, jsonString);
    }

    @Override
    public List<String> getPrefixes(MessageCreateEvent event) {
        return super.getPrefixes(event.getMessageAuthor().getIdAsString());
    }
}
