package fr.epsilon.common.queue;

import com.google.gson.Gson;
import fr.epsilon.common.Epsilon;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class EQueueModule {
    private final OkHttpClient okHttp;
    private final Gson gson;

    public EQueueModule(OkHttpClient okHttp, Gson gson) {
        this.okHttp = okHttp;
        this.gson = gson;
    }

    public boolean join(String username, String queue) {
        return join(Collections.singletonList(username), queue);
    }

    public boolean join(List<String> players, String queue) {
        EQueuePlayer queuePlayer = new EQueuePlayer(players, queue);
        byte[] json = gson.toJson(queuePlayer).getBytes(StandardCharsets.UTF_8);

        Request request = new Request.Builder()
                .url(Epsilon.get().getEpsilonURL("/queue/push"))
                .post(RequestBody.create(json))
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
