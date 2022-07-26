package fr.epsilon.exporter.commands;

import fr.epsilon.exporter.EpsilonExporter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;

public class HubCommand extends Command {
    private final EpsilonExporter main;

    public HubCommand(EpsilonExporter main) {
        super("hub", null, "lobby");

        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;

            player.sendMessage(new ComponentBuilder("Connexion au hub ...").color(ChatColor.RED).create());

            List<ServerInfo> hubs = main.getRegister().getHubs();

            player.connect(hubs.get(0));
        }
    }
}
