package fr.epsilon.common;

import com.google.gson.Gson;
import fr.epsilon.common.crd.EpsilonInstanceCRD;
import fr.epsilon.common.crd.EpsilonInstanceCRDList;
import fr.epsilon.common.informer.InstanceInformer;
import fr.epsilon.common.instance.EInstance;
import fr.epsilon.common.instance.EInstanceModule;
import fr.epsilon.common.queue.EQueueModule;
import fr.epsilon.common.template.ETemplate;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import okhttp3.*;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Epsilon {
    private static Epsilon singleton;

    private final String name;

    private final EInstanceModule instanceModule;
    private final EQueueModule queueModule;

    private String namespace;

    private SharedInformerFactory informerFactory;
    private InstanceInformer instanceInformer;

    private ETemplate template;

    public Epsilon() {
        Path namespacePath = Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/namespace");

        try (Stream<String> lines = Files.lines(namespacePath)) {
            this.namespace = lines.collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ApiClient kubeClient = Config.defaultClient();
            GenericKubernetesApi<EpsilonInstanceCRD, EpsilonInstanceCRDList> epsilonInstanceClient =
                    new GenericKubernetesApi<>(EpsilonInstanceCRD.class, EpsilonInstanceCRDList.class, "controller.epsilon.fr", "v1", "epsiloninstances", kubeClient);

            this.informerFactory = new SharedInformerFactory(kubeClient);
            this.instanceInformer = new InstanceInformer(namespace, informerFactory, epsilonInstanceClient);
        } catch (IOException e) {
            e.printStackTrace();
        }

        OkHttpClient okHttp = new OkHttpClient();
        Gson gson = new Gson();

        try (FileReader reader = new FileReader("./details.epsilon")) {
            this.template = gson.fromJson(reader, ETemplate.class);
        } catch (IOException e) {
            Logger.getAnonymousLogger().info("Read details.epsilon failed.");
        }

        this.name = System.getenv("HOSTNAME");

        this.instanceModule = new EInstanceModule(okHttp, gson);
        this.queueModule = new EQueueModule(okHttp, gson);
    }

    public static Epsilon get() {
        if (singleton == null)
            singleton = new Epsilon();

        return singleton;
    }

    public InstanceInformer runInstanceInformer() {
        informerFactory.startAllRegisteredInformers();

        return instanceInformer;
    }

    public String name() {
        return name;
    }

    public ETemplate template() {
        return template;
    }

    public CompletableFuture<EInstance> instance() {
        return instanceModule.getInstance(name);
    }

    public EInstanceModule instanceModule() {
        return instanceModule;
    }

    public EQueueModule queueModule() {
        return queueModule;
    }
}
