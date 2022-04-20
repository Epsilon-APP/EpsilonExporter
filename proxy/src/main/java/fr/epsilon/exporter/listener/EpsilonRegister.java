package fr.epsilon.exporter.listener;

import com.google.gson.reflect.TypeToken;
import fr.epsilon.common.Epsilon;
import fr.epsilon.exporter.EpsilonExporter;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodCondition;
import io.kubernetes.client.openapi.models.V1PodStatus;
import io.kubernetes.client.util.Watch;
import net.md_5.bungee.api.config.ServerInfo;
import okhttp3.Call;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EpsilonRegister implements Runnable {
    private final static String INSTANCE_TYPE_LABEL = "epsilon.fr/instance";
    private final static String TEMPLATE_LABEL = "epsilon.fr/template";

    private final static String TEMPLATE_LOBBY = "hub";

    private EpsilonExporter main;
    private ConcurrentMap<String, ServerInfo> hubs;

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

    public List<ServerInfo> getHubs() {
        return hubs.values().stream()
                .sorted(Comparator.comparingInt(o -> o.getPlayers().size()))
                .collect(Collectors.toList());
    }

    public void removeServer(String name) {
        main.getProxy().getServers().remove(name);

        hubs.remove(name);

        main.getLogger().info("Delete server " + name);
    }

    @Override
    public void run() {
        while (true) {
            try {
                main.getLogger().info("Watch started");

                CoreV1Api api = Epsilon.get().getKubeApi();
                ApiClient client = Epsilon.get().getKubeClient();

                Call call = api.listNamespacedPodCall(namespace, "false", null, null, null, INSTANCE_TYPE_LABEL + "=server", null, null, null, null, true, null);

                try (Watch<V1Pod> watch = Watch.createWatch(client, call, new TypeToken<Watch.Response<V1Pod>>(){}.getType())) {
                    for (Watch.Response<V1Pod> event : watch) {
                        V1Pod pod = event.object;

                        V1ObjectMeta metadata = pod.getMetadata();
                        V1PodStatus status = pod.getStatus();

                        if (metadata != null && status != null) {
                            List<V1PodCondition> conditions = pod.getStatus().getConditions();

                            if (conditions != null) {
                                String name = metadata.getName();

                                switch (event.type) {
                                    case "ADDED":
                                    case "MODIFIED":
                                        boolean ready = pod.getStatus().getConditions().stream()
                                                .filter(condition -> condition.getType().equals("Ready"))
                                                .map(condition -> condition.getStatus().equals("True"))
                                                .findFirst()
                                                .orElse(false);

                                        if (ready) {
                                            ServerInfo server = main.getProxy().constructServerInfo(name,
                                                    new InetSocketAddress(Objects.requireNonNull(status.getPodIP()), 25565),
                                                    "",
                                                    false);

                                            main.getProxy().getServers().put(name, server);

                                            Map<String, String> labels = metadata.getLabels();

                                            if (labels != null) {
                                                String label = labels.get(TEMPLATE_LABEL);

                                                if (label != null && label.equals(TEMPLATE_LOBBY))
                                                    hubs.put(name, server);
                                            }

                                            main.getLogger().info("Add server " + name);
                                        }

                                        break;

                                    case "DELETED":
                                        removeServer(name);
                                        break;
                                }
                            }
                        }
                    }
                    main.getLogger().info("Watch is down !");
                }
            }catch (ApiException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
