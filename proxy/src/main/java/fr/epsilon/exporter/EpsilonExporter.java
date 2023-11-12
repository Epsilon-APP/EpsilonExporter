package fr.epsilon.exporter;

import fr.epsilon.common.Epsilon;
import fr.epsilon.exporter.listener.EpsilonConnection;
import fr.epsilon.exporter.listener.EpsilonRegister;
import net.md_5.bungee.api.plugin.Plugin;

public class EpsilonExporter extends Plugin {
    private Epsilon epsilon;
    private EpsilonRegister register;

    @Override
    public void onEnable() {
        getProxy().getLogger().info("Epsilon ignited !");

        this.epsilon = Epsilon.get();

        EpsilonConnection connection = new EpsilonConnection(this);

        getProxy().getPluginManager().registerListener(this, connection);

        getProxy().setReconnectHandler(connection);
        getProxy().getServers().clear();

        this.register = new EpsilonRegister(this);
        register.run();

        EpsilonEventStream.init(this);

        EpsilonReachTask.init(this);
    }

    public Epsilon getEpsilon() {
        return epsilon;
    }

    public EpsilonRegister getRegister() {
        return register;
    }
}
