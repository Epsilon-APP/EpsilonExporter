package fr.epsilon.exporter;

import com.launchdarkly.eventsource.EventSource;
import fr.epsilon.common.Epsilon;
import fr.epsilon.exporter.listener.EpsilonEventListener;

import java.net.URI;
import java.time.Duration;

public class EpsilonEventStream {
    private final EpsilonEventListener listener;
    private EventSource eventSource;

    private EpsilonEventStream(EpsilonExporter main) {
        this.listener = new EpsilonEventListener(main, this);

        load();
    }

    public static EpsilonEventStream init(EpsilonExporter main) {
        return new EpsilonEventStream(main);
    }

    private void load() {
        String url = Epsilon.get().getEpsilonURL("/api/events");
        URI uri = URI.create(url);

        this.eventSource = new EventSource.Builder(listener, uri)
                .reconnectTime(Duration.ofMillis(100))
                .maxReconnectTime(Duration.ofSeconds(100))
                .build();

        eventSource.start();
    }
}
