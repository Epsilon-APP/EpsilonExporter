package fr.epsilon.common;

import com.google.gson.Gson;
import fr.epsilon.common.crd.EpsilonInstanceCRD;
import fr.epsilon.common.crd.EpsilonInstanceCRDList;
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
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class Epsilon {
    private static Epsilon singleton;

    private final String name;

    private final EInstanceModule instanceModule;
    private final EQueueModule queueModule;

    private SharedInformerFactory informerFactory;
    private GenericKubernetesApi<EpsilonInstanceCRD, EpsilonInstanceCRDList> epsilonInstanceClient;

    private ETemplate template;

    public Epsilon() {
        try {
            ApiClient kubeClient = Config.defaultClient();

            this.informerFactory = new SharedInformerFactory(kubeClient);
            this.epsilonInstanceClient = new GenericKubernetesApi<>(EpsilonInstanceCRD.class, EpsilonInstanceCRDList.class, "controller.epsilon.fr", "v1", "epsiloninstances", kubeClient);
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

    public SharedInformerFactory getInformerFactory() {
        return informerFactory;
    }

    public GenericKubernetesApi<EpsilonInstanceCRD, EpsilonInstanceCRDList> getEpsilonInstanceClient() {
        return epsilonInstanceClient;
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
