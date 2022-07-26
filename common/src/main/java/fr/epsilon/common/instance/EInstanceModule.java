package fr.epsilon.common.instance;

import com.google.gson.Gson;
import fr.epsilon.common.utils.EpsilonEnvironments;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class EInstanceModule {
    private final OkHttpClient okHttp;
    private final Gson gson;

    public EInstanceModule(OkHttpClient okHttp, Gson gson) {
        this.okHttp = okHttp;
        this.gson = gson;
    }

    public CompletableFuture<EInstance> getInstance(String instance) {
        CompletableFuture<EInstance> future = new CompletableFuture<>();

        Request request = new Request.Builder()
                .url(EpsilonEnvironments.getEpsilonURL("/instance/get_from_name/" + instance))
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
                .url(EpsilonEnvironments.getEpsilonURL("/instance" + (template != null ? "/get/" + template : "/get_all")))
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
                .url(EpsilonEnvironments.getEpsilonURL("/instance/create/" + template))
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
}
