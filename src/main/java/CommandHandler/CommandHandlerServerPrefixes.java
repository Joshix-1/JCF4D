package CommandHandler;

import com.fasterxml.jackson.databind.JsonNode;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import util.JCF4DExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CommandHandlerServerPrefixes extends CommandHandlerPrefixes {

    public CommandHandlerServerPrefixes(DiscordApi api, JCF4DExceptionHandler exceptionHandler, String defaultPrefix, JsonNode prefixes) {
        super(api, exceptionHandler, defaultPrefix, prefixes);
    }

    public CommandHandlerServerPrefixes(DiscordApi api, JCF4DExceptionHandler exceptionHandler, String defaultPrefix, File file) throws IOException {
        super(api, exceptionHandler, defaultPrefix, file);
    }

    protected CommandHandlerServerPrefixes(DiscordApi api, JCF4DExceptionHandler exceptionHandler, String defaultPrefix, String jsonString) throws IOException {
        super(api, exceptionHandler, defaultPrefix, jsonString);
    }


    @Override
    public List<String> getPrefixes(MessageCreateEvent event) {
        return event.getServer().map(Server::getIdAsString).map(super::getPrefixes).orElse(super.getPrefixes(event));
    }
}
