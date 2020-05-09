package Command;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.event.message.MessageCreateEvent;
import util.CommandPermission;

import java.awt.*;
import java.util.Optional;

public interface Command {

    String[] getAliases();

    boolean run(MessageCreateEvent event, EmbedBuilder defaultEmbed, String[] args);

    boolean shouldBeCleanedUp();

    String getName();

    Optional<Color> getColor();

    String getFooterMessage();

    void onError(Exception e);

    CommandPermission getNeededPermission();
}
