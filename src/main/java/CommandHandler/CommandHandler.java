package CommandHandler;

import Command.Command;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;
import util.*;

import java.util.*;
import java.util.regex.Pattern;


public class CommandHandler {
    private final static Pattern WHITE_SPACES = Pattern.compile("\\s+");
    private final DiscordApi api;
    private final HashMap<String, Command> aliases;
    private List<String> prefixes;
    private Optional<JCF4DEmoji> successfulEmoji = Optional.empty();
    private Optional<JCF4DEmoji> unsuccessfulEmoji = Optional.empty();
    private JCF4DExceptionHandler exceptionHandler;

    public CommandHandler(DiscordApi api, JCF4DExceptionHandler exceptionHandler, String... prefixes) {
        this.api = api;
        this.exceptionHandler = exceptionHandler;
        this.prefixes = new ArrayList<>();
        this.aliases = new HashMap<>();

        this.prefixes.addAll(Arrays.asList(prefixes));
        addMentionPrefixes(this.prefixes);

        api.addMessageDeleteListener(new CommandCleanupListener());
        api.addMessageCreateListener(this::runCommands);
    }

    public CommandHandler addCommand(Command command) {
        addAliases(command);
        return this;
    }

    public CommandHandler addCommands(Command... commands) {
        return addCommands(Arrays.asList(commands));
    }

    public CommandHandler addCommands(List<Command> commands) {
        commands.forEach(this::addAliases);
        return this;
    }

    private void runCommands(MessageCreateEvent event) {
        if(aliases.size() == 0 || !event.getMessageAuthor().isRegularUser()) {
            return;
        }

        String message = event.getMessageContent().toLowerCase();

        for (String prefix : getPrefixes(event)) {
            if(message.startsWith(prefix.toLowerCase())) {
                String[] messageArr = WHITE_SPACES.split(message.substring(prefix.length()));

                int commandIndex = Math.min(messageArr[0].isEmpty() ? 1 : 0, messageArr.length -1);

                String commandName = messageArr[commandIndex];
                if(aliases.containsKey(commandName)) {
                    String[] args = WHITE_SPACES.split(event.getMessageContent().substring(message.indexOf(commandName) + commandName.length()));

                    api.getThreadPool().getExecutorService().submit(() -> run(event, aliases.get(commandName), args));
                }
                break;
            }
        }
    }

    private void run(MessageCreateEvent event, Command command, String[] args) {
        try {
            CommandPermission missingPermissions = command.getNeededPermission().getMissingPermissions(event);
            if(missingPermissions.isEmpty()) {
                if (command.run(event, JCF4DUtils.createEmbed(event, command), args)) {
                    successfulEmoji.ifPresent(jcf4DEmoji -> jcf4DEmoji.addReactionToMessage(event).exceptionally(exceptionHandler::handleThrowable));
                } else {
                    unsuccessfulEmoji.ifPresent(jcf4DEmoji -> jcf4DEmoji.addReactionToMessage(event).exceptionally(exceptionHandler::handleThrowable));
                }
            } else {
                unsuccessfulEmoji.ifPresent(jcf4DEmoji -> jcf4DEmoji.addReactionToMessage(event).exceptionally(exceptionHandler::handleThrowable));
                exceptionHandler.handleMissingPermissions(event, command, missingPermissions);
            }
        } catch(Exception e) {
            unsuccessfulEmoji.ifPresent(jcf4DEmoji -> jcf4DEmoji.addReactionToMessage(event).exceptionally(exceptionHandler::handleThrowable));
            successfulEmoji.ifPresent(jcf4DEmoji -> jcf4DEmoji.removeReactionFromMessage(event).exceptionally(exceptionHandler::handleThrowable));
            exceptionHandler.handleCommandExecutionException(event, command, e);
        }
    }

    public List<String> getPrefixes(MessageCreateEvent event) {
        return getPrefixes();
    }

    public List<String> getPrefixes() {
        return prefixes;
    }

    protected void addMentionPrefixes(List<String> listToAdd) {
        listToAdd.add(api.getYourself().getMentionTag());
        listToAdd.add(api.getYourself().getNicknameMentionTag());
    }

    public void addFeedBackEmojis(JCF4DEmoji successful, JCF4DEmoji unsuccessful) {
        this.successfulEmoji = Optional.ofNullable(successful);
        this.unsuccessfulEmoji = Optional.ofNullable(unsuccessful);
    }

    public void addAliases(Command command) {
        for (String alias : command.getAliases()) {
            aliases.put(alias.toLowerCase(), command);
        }
    }
}