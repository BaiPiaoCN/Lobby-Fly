
package org.baipiaocn.lobby_fly;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class LobbyFly extends JavaPlugin {

    private FileConfiguration messagesConfig;
    private File messagesFile;

    @Override
    public void onEnable() {
        createMessagesFileIfNotExists();
        loadMessages();
        getCommand("fly").setExecutor(new FlyCommandExecutor());
        getLogger().info("Lobby Fly plugin enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("[LobbyFly] Plugin disabled.");
    }

    private void createMessagesFileIfNotExists() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
    }

    private void loadMessages() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // If plugin version updates, need to load new default values
        InputStream in = getResource("messages.yml");
        if (in != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(in));
            messagesConfig.setDefaults(defaultConfig);
        }
    }

    private class FlyCommandExecutor implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                getLogger().warning("Only players can use this command!");
                return true;
            }

            Player player = (Player) sender;

            if (!player.hasPermission("lobby.fly")) {
                sendErrorMessage(player, "no_permission");
                return true;
            }

            toggleFlight(player);
            return true;
        }
    }

    private void toggleFlight(Player player) {
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            sendSuccessMessage(player, "disabled");
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            sendSuccessMessage(player, "enabled");
        }
    }

    private void sendErrorMessage(Player player, String key) {
        String message = messagesConfig.getString("fly." + key);
        if (message != null) {
            player.sendMessage(message.replace('&', '§'));
        } else {
            player.sendMessage("An error occurred while retrieving the message.");
        }
    }

    private void sendSuccessMessage(Player player, String key) {
        String message = messagesConfig.getString("fly." + key);
        if (message != null) {
            player.sendMessage(message.replace('&', '§'));
        } else {
            player.sendMessage("An error occurred while retrieving the message.");
        }
    }
}