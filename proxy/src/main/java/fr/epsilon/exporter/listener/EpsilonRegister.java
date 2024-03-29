package fr.epsilon.exporter.listener;

import fr.epsilon.api.informer.EInstanceInformerListener;
import fr.epsilon.api.instance.EInstance;
import fr.epsilon.api.instance.EState;
import fr.epsilon.api.instance.EType;
import fr.epsilon.common.informer.InstanceInformer;
import fr.epsilon.common.instance.Instance;
import fr.epsilon.exporter.EpsilonExporter;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class EpsilonRegister {
    private final EpsilonExporter main;
    private final ConcurrentMap<String, ServerInfo> hubs;

    private final InstanceInformer instanceInformer;

    public EpsilonRegister(EpsilonExporter main) {
        this.main = main;
        this.hubs = new ConcurrentHashMap<>();

        this.instanceInformer = main.getEpsilon().runInstanceInformer();
    }

    public void run() {
        for (Instance instance : instanceInformer.getInstances())
            registerInstance(instance);

        instanceInformer.registerListener(new EInstanceInformerListener() {
            @Override
            public void onInstanceUpdate(EInstance instance) {
                registerInstance(instance);
            }

            @Override
            public void onInstanceRemove(EInstance instance) {
                unregisterInstance(instance);
            }
        });
    }

    public List<ServerInfo> getHubs() {
        return hubs.values().stream()
                .sorted(Comparator.comparingInt(o -> o.getPlayers().size()))
                .collect(Collectors.toList());
    }

    private void registerInstance(EInstance instance) {
        String name = instance.getName();

        boolean isHub = instance.isHub();

        EType type = instance.getType();
        EState state = instance.getState();

        String ip = instance.getLocalIp();

        if (type == EType.Server && state == EState.Running) {
            ServerInfo server = main.getProxy().constructServerInfo(name,
                    new InetSocketAddress(Objects.requireNonNull(ip), 25565),
                    "",
                    false);

            main.getProxy().getServers().put(name, server);

            if (isHub)
                hubs.put(name, server);
            else
                hubs.remove(name, server);

            main.getLogger().info("Add server " + name);
        }
    }

    private void unregisterInstance(EInstance instance) {
        String name = instance.getName();

        main.getProxy().getServers().remove(name);

        hubs.remove(name);

        main.getLogger().info("Delete server " + name);
    }
}
