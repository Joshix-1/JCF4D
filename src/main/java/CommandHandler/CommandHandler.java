package CommandHandler;

import Command.Command;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.event.message.MessageCreateEvent;
import util.CommandCleanupListener;
import util.CommandPermission;
import util.JCF4DUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;


public class CommandHandler {
    private final static Pattern WHITE_SPACES = Pattern.compile("\\s+");
    private final DiscordApi api;
    private final List<Command> commands;
    private List<String> prefixes;
    private Optional<Emoji> successfulEmoji = Optional.empty();
    private Optional<Emoji> unsuccessfulEmoji = Optional.empty();
    private Optional<String> successfulString = Optional.empty();
    private Optional<String> unsuccessfulString = Optional.empty();


    CommandHandler(DiscordApi api, String... prefixes) {
        this.api = api;
        this.prefixes = new ArrayList<>();
        this.commands = new ArrayList<>();

        this.prefixes.addAll(Arrays.asList(prefixes));
        addMentionPrefixes(this.prefixes);

        api.addMessageDeleteListener(new CommandCleanupListener());
        api.addMessageCreateListener(this::runCommands);
    }

    public CommandHandler addCommand(Command command) {
        commands.add(command);
        return this;
    }

    public CommandHandler addCommands(Command... commands) {
        this.commands.addAll(Arrays.asList(commands));
        return this;
    }

    public CommandHandler addCommands(List<Command> commands) {
        this.commands.addAll(commands);
        return this;
    }

    private void runCommands(MessageCreateEvent event) {
        if(commands.size() == 0 || !event.getMessageAuthor().isRegularUser()) {
            return;
        }

        String message = event.getMessageContent().toLowerCase();

        for (String prefix : getPrefixes(event)) {
            if(message.startsWith(prefix.toLowerCase())) {
                String[] messageArr = WHITE_SPACES.split(message.substring(prefix.length()));

                int commandIndex = Math.min(messageArr[0].isEmpty() ? 1 : 0, messageArr.length -1);

                String[] args = Arrays.copyOfRange(messageArr, commandIndex + 1, messageArr.length);

                commands.forEach(command -> {
                    if(JCF4DUtils.arrayContainsString(command.getAliases(), messageArr[commandIndex])) {
                        api.getThreadPool().getExecutorService().submit(() -> run(event, command, args));
                    }
                });
                break;
            }
        }
    }

    private void run(MessageCreateEvent event, Command command, String[] args) {
        try {
            CommandPermission missingPermissions = command.getNeededPermission().getMissingPermissions(event);
            if(missingPermissions.isEmpty()) {
                if (command.run(event, JCF4DUtils.createEmbed(event, command), args)) {
                    addSuccesfulToMessage(event);
                } else {
                    addUnsuccesfulToMessage(event);
                }
            } else {
                JCF4DUtils.sendMissingPermissionErrorMessage(event, command, missingPermissions);
            }
        } catch(Exception e) {
            command.onError(e);
            addUnsuccesfulToMessage(event);
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

    private void addUnsuccesfulToMessage(MessageCreateEvent event) {
        unsuccessfulEmoji.ifPresent(event::addReactionToMessage);
        unsuccessfulString.ifPresent(event::addReactionToMessage);
    }

    private void addSuccesfulToMessage(MessageCreateEvent event) {
        successfulEmoji.ifPresent(event::addReactionToMessage);
        successfulString.ifPresent(event::addReactionToMessage);
    }

    public void setFeedBackEmojis(Emoji successful, Emoji unsuccessful) {
        this.successfulEmoji = Optional.ofNullable(successful);
        this.unsuccessfulEmoji = Optional.ofNullable(unsuccessful);

        this.unsuccessfulString = Optional.empty();
        this.successfulString = Optional.empty();
    }

    public void setFeedBackEmojis(String successful, String unsuccessful) {
        this.successfulString = Optional.ofNullable(successful);
        this.unsuccessfulString = Optional.ofNullable(unsuccessful);

        this.successfulEmoji = Optional.empty();
        this.unsuccessfulEmoji = Optional.empty();
    }
}