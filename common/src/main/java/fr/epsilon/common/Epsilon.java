package fr.epsilon.common;

import com.google.gson.Gson;
import fr.epsilon.api.EpsilonAPI;
import fr.epsilon.common.crd.EpsilonInstanceCRD;
import fr.epsilon.common.crd.EpsilonInstanceCRDList;
import fr.epsilon.common.informer.InstanceInformer;
import fr.epsilon.common.instance.Instance;
import fr.epsilon.common.instance.InstanceModule;
import fr.epsilon.common.queue.QueueModule;
import fr.epsilon.common.template.Template;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import okhttp3.OkHttpClient;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Epsilon extends EpsilonAPI {
    private static Epsilon singleton;

    private final String name;

    private final InstanceModule instanceModule;
    private final QueueModule queueModule;

    private String namespace;

    private SharedInformerFactory informerFactory;
    private InstanceInformer instanceInformer;

    private Template template;

    public Epsilon() {
        this(true);
    }

    public Epsilon(boolean internal) {
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

        if (internal) {
            try (FileReader reader = new FileReader("./details.epsilon")) {
                this.template = gson.fromJson(reader, Template.class);
            } catch (IOException e) {
                Logger.getAnonymousLogger().info("Read details.epsilon failed.");
            }
        }

        this.name = System.getenv("HOSTNAME");

        this.instanceModule = new InstanceModule(okHttp, gson);
        this.queueModule = new QueueModule(okHttp, gson);
    }

    public static Epsilon get() {
        if (singleton == null)
            singleton = new Epsilon();

        return singleton;
    }

    public static Epsilon initExternal() {
        singleton = new Epsilon(false);
        return singleton;
    }

    @Override
    public InstanceInformer runInstanceInformer() {
        informerFactory.startAllRegisteredInformers();

        return instanceInformer;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Template template() {
        return template;
    }

    @Override
    public CompletableFuture<? extends Instance> instance() {
        return instanceModule.getInstance(name);
    }

    @Override
    public InstanceModule instanceModule() {
        return instanceModule;
    }

    @Override
    public QueueModule queueModule() {
        return queueModule;
    }

    @Override
    public boolean isReachable() {
        return true;
    }
}
