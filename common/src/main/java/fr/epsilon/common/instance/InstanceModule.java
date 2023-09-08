package fr.epsilon.common.instance;

import com.google.gson.Gson;
import fr.epsilon.api.instance.EInstance;
import fr.epsilon.api.instance.EInstanceModule;
import fr.epsilon.api.instance.EType;
import fr.epsilon.common.utils.EpsilonEnvironments;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class InstanceModule extends EInstanceModule {
    private final OkHttpClient okHttp;
    private final Gson gson;

    public InstanceModule(OkHttpClient okHttp, Gson gson) {
        this.okHttp = okHttp;
        this.gson = gson;
    }

    @Override
    public CompletableFuture<? extends Instance> getInstance(String name) {
        CompletableFuture<Instance> future = new CompletableFuture<>();

        Request request = new Request.Builder()
                .url(EpsilonEnvironments.getEpsilonURL("/instance/get/" + name))
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
                    Instance instance = gson.fromJson(body.string(), Instance.class);

                    future.complete(instance);
                }
            }
        });

        return future;
    }

    @Override
    public CompletableFuture<? extends Instance[]> getInstances() {
        CompletableFuture<Instance[]> future = new CompletableFuture<>();

        Request request = new Request.Builder()
                .url(EpsilonEnvironments.getEpsilonURL("/instance/get_all"))
                .build();

        getInstances(future, request);

        return future;
    }

    @Override
    public CompletableFuture<? extends Instance[]> getInstances(String template) {
        CompletableFuture<Instance[]> future = new CompletableFuture<>();

        Request request = new Request.Builder()
                .url(EpsilonEnvironments.getEpsilonURL("/instance/get_from_template/" + template))
                .build();

        getInstances(future, request);

        return future;
    }

    @Override
    public CompletableFuture<? extends Instance[]> getInstances(EType type) {
        CompletableFuture<Instance[]> future = new CompletableFuture<>();

        getInstances().whenCompleteAsync((instances, error) -> {
            Instance[] array = Arrays.stream(instances)
                    .filter(instance -> instance.getType() == type)
                    .toArray(Instance[]::new);

            future.complete(array);
        });

        return future;
    }

    @Override
    public String openInstance(String template) {
        return openInstance(template, new Object());
    }

    @Override
    public <T> String openInstance(String template, T content) {
        MediaType media = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(gson.toJson(content), media);

        Request request = new Request.Builder()
                .url(EpsilonEnvironments.getEpsilonURL("/instance/create/" + template))
                .post(body)
                .build();

        try {
            Response response = okHttp.newCall(request).execute();

            ResponseBody bodyResponse = response.body();

            assert bodyResponse != null;
            String instanceName = bodyResponse.string();

            response.close();

            return instanceName;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean closeInstance(String name) {
        Request request = new Request.Builder()
                .url(EpsilonEnvironments.getEpsilonURL("/instance/close/" + name))
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

    @Override
    public boolean inGameInstance(String name) {
        Request request = new Request.Builder()
                .url(EpsilonEnvironments.getEpsilonURL("/instance/in_game/" + name))
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

    private void getInstances(CompletableFuture<Instance[]> future, Request request) {
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
                    InstanceList instances = gson.fromJson(body.string(), InstanceList.class);

                    future.complete(instances.getInstances());
                }
            }
        });
    }
}
