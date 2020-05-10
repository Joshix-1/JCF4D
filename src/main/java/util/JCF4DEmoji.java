package util;

import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.concurrent.CompletableFuture;

public class JCF4DEmoji {
    private String string;
    private Emoji emoji;

    public JCF4DEmoji(String emoji) {
        string = emoji;
    }

    public JCF4DEmoji(Emoji emoji) {
        this.emoji = emoji;
    }

    public CompletableFuture<Void> addReactionToMessage(Message message) {
        if(emoji != null) {
            return message.addReaction(emoji);
        } else {
            return message.addReaction(string);
        }
    }

    public CompletableFuture<Void> removeReactionFromMessage(Message message) {
        if(emoji != null) {
            return message.removeReactionByEmoji(emoji);
        } else {
            return message.removeReactionByEmoji(string);
        }
    }

    public CompletableFuture<Void> addReactionToMessage(MessageCreateEvent event) {
        return addReactionToMessage(event.getMessage());
    }

    public CompletableFuture<Void> removeReactionFromMessage(MessageCreateEvent event) {
        return removeReactionFromMessage(event.getMessage());
    }
}
