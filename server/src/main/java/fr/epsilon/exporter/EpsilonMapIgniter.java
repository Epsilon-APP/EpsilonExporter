package fr.epsilon.exporter;

import fr.epsilon.common.Epsilon;
import fr.epsilon.common.template.ETemplate;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class EpsilonMapIgniter {
    private final EpsilonExporter main;

    private final ETemplate template;
    private final OkHttpClient client;
    private final File destination;

    public EpsilonMapIgniter(EpsilonExporter main, ETemplate template) {
        this.main = main;

        this.template = template;
        this.client = new OkHttpClient();
        this.destination = new File("./");
    }

    public void download() {
        for (String map : template.getMaps()) {
            downloadAndExtract(map);
        }
    }

    public void load() {
        World defaultMap = main.getServer().getWorlds().get(0);

        for (String map : template.getMaps()) {
            if (!map.equals(defaultMap.getName()))
                new WorldCreator(map).createWorld();
        }
    }

    private void downloadAndExtract(String map) {
        try {
            String url = Epsilon.get().getEpsilonTemplateURL("/maps/" + map + "/get");

            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();

                try (InputStream file = responseBody.byteStream()) {
                    ZipFolder.unzip(file, new File(destination, map));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
