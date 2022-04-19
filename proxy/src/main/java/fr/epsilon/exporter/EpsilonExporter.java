package fr.epsilon.exporter;

import fr.epsilon.exporter.commands.HubCommand;
import fr.epsilon.exporter.listener.EpsilonConnection;
import fr.epsilon.exporter.listener.EpsilonRegister;
import net.md_5.bungee.api.plugin.Plugin;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EpsilonExporter extends Plugin {
    private EpsilonRegister register;

    @Override
    public void onEnable() {
        getProxy().getLogger().info("Epsilon ignited !");

        EpsilonConnection connection = new EpsilonConnection(this);

        getProxy().getPluginManager().registerListener(this, connection);

        getProxy().setReconnectHandler(connection);
        getProxy().getServers().clear();

        this.register = new EpsilonRegister(this);

        getProxy().getScheduler().runAsync(this, register);

        EpsilonEventStream.init(this);

        getProxy().getPluginManager().registerCommand(this, new HubCommand(this));

        try {
            Path path = Paths.get("epsilon_start");
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public EpsilonRegister getRegister() {
        return register;
    }
}
