package fr.epsilon.exporter.listener;

import fr.epsilon.common.Epsilon;
import fr.epsilon.common.crd.EpsilonInstance;
import fr.epsilon.common.crd.EpsilonInstanceList;
import fr.epsilon.common.crd.EpsilonInstanceStatus;
import fr.epsilon.common.instance.EState;
import fr.epsilon.common.instance.EType;
import fr.epsilon.exporter.EpsilonExporter;
import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.informer.SharedInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import io.kubernetes.client.util.generic.KubernetesApiResponse;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EpsilonRegister {
    private final EpsilonExporter main;
    private final ConcurrentMap<String, ServerInfo> hubs;

    private String namespace;

    public EpsilonRegister(EpsilonExporter main) {
        this.main = main;
        this.hubs = new ConcurrentHashMap<>();

        Path namespacePath = Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/namespace");

        try (Stream<String> lines = Files.lines(namespacePath)) {
            this.namespace = lines.collect(Collectors.joining(System.lineSeparator()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runInformer() {
        GenericKubernetesApi<EpsilonInstance, EpsilonInstanceList> epsilonInstanceClient = Epsilon.get().getEpsilonInstanceClient();
        SharedInformerFactory informerFactory = Epsilon.get().getInformerFactory();
        SharedInformer<EpsilonInstance> instanceInformer = informerFactory.sharedIndexInformerFor(epsilonInstanceClient,
                EpsilonInstance.class, TimeUnit.MINUTES.toMillis(10), namespace);

        try {
            KubernetesApiResponse<EpsilonInstanceList> instanceList = epsilonInstanceClient.list(namespace).throwsApiException();
            for (EpsilonInstance instance : instanceList.getObject().getItems())
                registerInstance(instance);
        } catch (ApiException e) {
            e.printStackTrace();
        }

        instanceInformer.addEventHandlerWithResyncPeriod(new ResourceEventHandler<EpsilonInstance>() {
            @Override
            public void onAdd(EpsilonInstance instance) {
            }

            @Override
            public void onUpdate(EpsilonInstance oldInstance, EpsilonInstance newInstance) {
                registerInstance(newInstance);
            }

            @Override
            public void onDelete(EpsilonInstance instance, boolean deletedFinalStateUnknown) {
                V1ObjectMeta metadata = instance.getMetadata();
                String name = metadata.getName();

                unregisterServer(name);
            }
        }, TimeUnit.MINUTES.toMillis(10));

        instanceInformer.run();
    }

    public List<ServerInfo> getHubs() {
        return hubs.values().stream()
                .sorted(Comparator.comparingInt(o -> o.getPlayers().size()))
                .collect(Collectors.toList());
    }

    private void registerInstance(EpsilonInstance instance) {
        EpsilonInstanceStatus status = instance.getStatus();

        if (status != null) {
            String name = instance.getName();

            EType type = status.getType();
            EState state = status.getState();
            String ip = status.getIp();

            if (type == EType.Server && state == EState.Running) {
                ServerInfo server = main.getProxy().constructServerInfo(name,
                        new InetSocketAddress(Objects.requireNonNull(ip), 25565),
                        "",
                        false);

                main.getProxy().getServers().put(name, server);

                if (status.isHub())
                    hubs.put(name, server);
                else
                    hubs.remove(name, server);

                main.getLogger().info("Add server " + name);
            }
        }
    }

    private void unregisterServer(String name) {
        main.getProxy().getServers().remove(name);

        hubs.remove(name);

        main.getLogger().info("Delete server " + name);
    }
}
