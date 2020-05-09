package util;

import Command.Command;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Scanner;

public class JCF4DUtils {
    static private final SecureRandom SECURE_RANDOM = new SecureRandom();

    static public String readTextFromFile(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner sc = new Scanner(file);

        while (sc.hasNext()) {
            stringBuilder.append(sc.next());
        }

        return stringBuilder.toString();
    }

    static public boolean arrayContainsString(String[] arr, String str) {
        for (int i = 0; i < arr.length; i++) {
            if(arr[i].equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    static public EmbedBuilder createEmbed(MessageCreateEvent event, Command command) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTimestamp(event.getMessage().getCreationTimestamp());

        embed.setTitle(command.getName());
        embed.setColor(command.getColor().orElse(getRandomColor()));

        String footerContent = command.getFooterMessage().replaceAll("(?i)<user>", event.getMessageAuthor().getDiscriminatedName());

        if(command.shouldBeCleanedUp()) {
            CommandCleanupListener.insertResponseTracker(embed, event.getMessageId(), footerContent, event.getMessageAuthor().getAvatar());
        } else {
            embed.setFooter(footerContent, event.getMessageAuthor().getAvatar());
        }

        return embed;
    }

    static public void sendMissingPermissionErrorMessage (MessageCreateEvent event, Command command, CommandPermission missingPermissions) throws Exception {
        EmbedBuilder embed = createEmbed(event, command).setColor(Color.RED);

        missingPermissions.forEach(permissionType -> embed.addField("\u200B", permissionType.toString()));

        event.getChannel().sendMessage(embed).join();
    }

    static public Color getRandomColor() {
        return new Color(getRandom(0, 255), getRandom(0, 255), getRandom(0, 255));
    }

    static public int getRandom(int min, int max) {
        int bound = max - min + 1;
        if (bound <= 0) return min;

        return SECURE_RANDOM.nextInt(bound) + min;
    }
}
