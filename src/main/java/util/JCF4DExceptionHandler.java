package util;

import Command.Command;
import org.javacord.api.event.message.MessageCreateEvent;

public interface JCF4DExceptionHandler {

    void handleMissingPermissions(MessageCreateEvent event, Command command, CommandPermission missingPermissions);

    void handleCommandExecutionException(MessageCreateEvent event, Command command, Exception e);

    Void handleThrowable(Throwable t);
}
