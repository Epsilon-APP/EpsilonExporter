package fr.epsilon.common;

import com.google.gson.Gson;
import fr.epsilon.common.crd.EpsilonInstance;
import fr.epsilon.common.crd.EpsilonInstanceList;
import fr.epsilon.common.instance.EInstance;
import fr.epsilon.common.instance.EInstanceList;
import fr.epsilon.common.queue.EQueueModule;
import fr.epsilon.common.template.ETemplate;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class Epsilon {
    private static Epsilon singleton;

    private final OkHttpClient okHttp;
    private final Gson gson;
    private final EQueueModule queueModule;

    private ApiClient kubeClient;
    private CoreV1Api kubeApi;

    private GenericKubernetesApi<EpsilonInstance, EpsilonInstanceList> epsilonInstanceClient;
    private SharedInformerFactory informerFactory;

    private ETemplate template;

    public Epsilon() {
        try {
            this.kubeClient = Config.defaultClient();
            this.kubeApi = new CoreV1Api(kubeClient);

            this.epsilonInstanceClient = new GenericKubernetesApi<>(EpsilonInstance.class, EpsilonInstanceList.class, "controller.epsilon.fr", "v1", "epsiloninstances", kubeClient);

            this.informerFactory = new SharedInformerFactory();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.okHttp = new OkHttpClient();
        this.gson = new Gson();

        try (FileReader reader = new FileReader("./details.epsilon")) {
            this.template = gson.fromJson(reader, ETemplate.class);
        } catch (IOException e) {
            Logger.getAnonymousLogger().info("Read details.epsilon failed.");
        }

        this.queueModule = new EQueueModule(okHttp, gson);
    }

    public static Epsilon get() {
        if (singleton == null)
            singleton = new Epsilon();

        return singleton;
    }

    public ApiClient getKubeClient() {
        return kubeClient;
    }

    public CoreV1Api getKubeApi() {
        return kubeApi;
    }

    public GenericKubernetesApi<EpsilonInstance, EpsilonInstanceList> getEpsilonInstanceClient() {
        return epsilonInstanceClient;
    }

    public SharedInformerFactory getInformerFactory() {
        return informerFactory;
    }

    public String name() {
        return System.getenv("HOSTNAME");
    }

    public ETemplate template() {
        return template;
    }

    public CompletableFuture<EInstance> instance() {
        return getInstance(name());
    }

    public void shutdown() {
        closeInstance(name());
    }

    public EQueueModule queueModule() {
        return queueModule;
    }

    public CompletableFuture<EInstance> getInstance(String instance) {
        CompletableFuture<EInstance> future = new CompletableFuture<>();

        Request request = new Request.Builder()
                .url(getEpsilonURL("/instance/get_from_name/" + instance))
                .build();

        okHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody body = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    assert body != null;
                    EInstance instance = gson.fromJson(body.string(), EInstance.class);

                    future.complete(instance);
                }
            }
        });

        return future;
    }

    public CompletableFuture<EInstance[]> getInstances(String template) {
        CompletableFuture<EInstance[]> future = new CompletableFuture<>();

        Request request = new Request.Builder()
                .url(getEpsilonURL("/instance" + (template != null ? "/get/" + template : "/get_all")))
                .build();

        okHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody body = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    assert body != null;
                    EInstanceList instances = gson.fromJson(body.string(), EInstanceList.class);

                    future.complete(instances.getInstances());
                }
            }
        });

        return future;
    }

    public boolean openInstance(String template) {
        Request request = new Request.Builder()
                .url(getEpsilonURL("/instance/create/" + template))
                .post(RequestBody.create(new byte[]{}))
                .build();

        try {
            Response response = okHttp.newCall(request).execute();
            boolean successful = response.isSuccessful();

            response.close();

            return successful;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean closeInstance(String name) {
        Request request = new Request.Builder()
                .url(getEpsilonURL("/instance/close/" + name))
                .post(RequestBody.create(new byte[]{}))
                .build();

        try {
            Response response = okHttp.newCall(request).execute();
            boolean successful = response.isSuccessful();

            response.close();

            return successful;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean inGameInstance(String name) {
        Request request = new Request.Builder()
                .url(getEpsilonURL("/instance/in_game/" + name))
                .post(RequestBody.create(new byte[]{}))
                .build();

        try {
            Response response = okHttp.newCall(request).execute();
            boolean successful = response.isSuccessful();

            response.close();

            return successful;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getEpsilonURL(String endpoint) {
        return "http://" + System.getenv("HOST_CONTROLLER") + ":8000" + endpoint;
    }

    public String getEpsilonTemplateURL(String endpoint) {
        return "http://" + System.getenv("HOST_TEMPLATE") + ":8000" + endpoint;
    }
}
