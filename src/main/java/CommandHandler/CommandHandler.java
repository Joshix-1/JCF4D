package CommandHandler;

import Command.Command;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;
import util.CommandCleanupListener;
import util.JCF4DUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class CommandHandler {
    private final static Pattern WHITE_SPACES = Pattern.compile("\\s+");
    private final DiscordApi api;
    private final List<Command> commands;
    private List<String> prefixes;

    CommandHandler(DiscordApi api, String... prefixes) {
        this.api = api;
        this.prefixes = new ArrayList<>();
        this.commands = new ArrayList<>();

        this.prefixes.addAll(Arrays.asList(prefixes));
        addMentionPrefixes(this.prefixes);

        api.addMessageDeleteListener(new CommandCleanupListener());
        api.addMessageCreateListener(this::runCommands);
    }

    public void addCommands(Command... commands) {
        this.commands.addAll(Arrays.asList(commands));
    }

    public void addCommands(List<Command> commands) {
        this.commands.addAll(commands);
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
            if (command.run(event, JCF4DUtils.createEmbed(event, command), args)) {
                event.addReactionToMessage("☑️").join();
            } else {
                event.addReactionToMessage("❌").join();
            }
        } catch(Exception e) {
            event.addReactionToMessage("❌").join();
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
}