package fr.epsilon.exporter.listener;

import com.google.gson.Gson;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import fr.epsilon.exporter.EpsilonEventStream;
import fr.epsilon.exporter.EpsilonExporter;
import fr.epsilon.exporter.objects.EpsilonServerClearPacket;
import fr.epsilon.exporter.objects.EpsilonServerPacket;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class EpsilonEventListener implements EventHandler {
    private static Gson GSON = new Gson();

    private EpsilonExporter main;
    private EpsilonEventStream eventStream;

    public EpsilonEventListener(EpsilonExporter main, EpsilonEventStream eventStream) {
        this.main = main;
        this.eventStream = eventStream;
    }

    @Override
    public void onOpen() {
        main.getLogger().info("Connection Open");
    }

    @Override
    public void onClosed() {
        main.getLogger().info("Connection Close");
    }

    @Override
    public void onMessage(String event, MessageEvent message) {
        EventType eventType = EventType.valueOf(event);

        if (eventType == EventType.SendToServer) {
            EpsilonServerPacket packet = GSON.fromJson(message.getData(), EpsilonServerPacket.class);

            for (String username : packet.getGroup().getPlayers()) {
                ServerInfo server = main.getProxy().getServerInfo(packet.getServer());
                ProxiedPlayer player = main.getProxy().getPlayer(username);

                player.connect(server);

                main.getLogger().info("Send player to " + server.getName());
            }
        }else if (eventType == EventType.ClearServer) {
            EpsilonServerClearPacket packet = GSON.fromJson(message.getData(), EpsilonServerClearPacket.class);
            String name = packet.getServer();

            main.getRegister().removeServer(name);

            main.getLogger().info("Clear server" + name);
        }
    }

    @Override
    public void onComment(String comment) {}

    @Override
    public void onError(Throwable throwable) {}

    enum EventType {
        SendToServer,
        ClearServer
    }
}
