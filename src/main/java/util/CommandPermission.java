package util;

import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CommandPermission {
    private final List<PermissionType> permissions;

    public CommandPermission() {
        this.permissions = new ArrayList<>();
    }

    public CommandPermission(PermissionType... permissions) {
        this.permissions = new ArrayList<>();
        this.permissions.addAll(Arrays.asList(permissions));
    }

    public int permissionCount() {
        return permissions.size();
    }

    public boolean isEmpty() {
        return permissionCount() == 0;
    }

    public void addPermissions(PermissionType... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
    }

    public void addPermission(PermissionType permission) {
        this.permissions.add(permission);
    }

    public CommandPermission getMissingPermissions(MessageCreateEvent event) {
        CommandPermission commandPermission = new CommandPermission();

        if(permissions.isEmpty() || !event.getServer().isPresent() || ! event.getMessageAuthor().asUser().isPresent()) {
            return commandPermission;
        }

        Server server = event.getServer().get();
        User user = event.getMessageAuthor().asUser().get();
        for (PermissionType permission : permissions) {
            if(!server.hasPermissions(user, permission)) {
                commandPermission.addPermission(permission);
            }
        }

        return commandPermission;
    }

    public void forEach(Consumer<? super PermissionType> action) {
        permissions.forEach(action);
    }
}
