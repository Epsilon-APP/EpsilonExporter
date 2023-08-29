package fr.epsilon.exporter.listener;

import fr.epsilon.common.template.Template;
import fr.epsilon.exporter.EpsilonExporter;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.Field;
import java.util.List;

public class EpsilonConnection implements ReconnectHandler, Listener {
    private final EpsilonExporter main;
    private final Template template;

    public EpsilonConnection(EpsilonExporter main) {
        this.main = main;
        this.template = main.getEpsilon().template();

        try {
            Class<?> configClass = main.getProxy().getConfig().getClass();

            if (!configClass.getSuperclass().equals(Object.class))
                configClass = configClass.getSuperclass();

            Field playerLimitField = configClass.getDeclaredField("playerLimit");

            playerLimitField.setAccessible(true);
            playerLimitField.setInt(main.getProxy().getConfig(), template.getSlots());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPing(ProxyPingEvent event) {
//        ServerPing ping = event.getResponse();
//        String visibleSlots = template.getLabels().getOrDefault("visible-slots", "1000");
//
//        ping.getPlayers().setMax(Integer.parseInt(visibleSlots));
    }

    @EventHandler
    public void serverKick(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo serverKick = event.getKickedFrom();

        player.sendMessage(new TextComponent("§cVous avez été redirigez dans un HUB"));

        List<ServerInfo> hubs = main.getRegister().getHubs();

        if (!hubs.isEmpty()) {
            event.setCancelServer(hubs.get(0));
            event.setCancelled(true);
        } else {
            event.setKickReasonComponent(new TextComponent[]{getKickReason()});
        }
    }

    @EventHandler
    public void serverPostLogin(PreLoginEvent event) {
        List<ServerInfo> hubs = main.getRegister().getHubs();

        if (hubs.isEmpty()) {
            event.setCancelReason(getKickReason());
            event.setCancelled(true);
        }
    }

    @Override
    public ServerInfo getServer(ProxiedPlayer player) {
        List<ServerInfo> hubs = main.getRegister().getHubs();

        if (!hubs.isEmpty())
            return hubs.get(0);

        return null;
    }

    @Override
    public void setServer(ProxiedPlayer player) {
    }

    @Override
    public void save() {
    }

    @Override
    public void close() {
    }

    private TextComponent getKickReason() {
        return new TextComponent("§cUn problème est survenu, veuillez réessayer !");
    }
}
