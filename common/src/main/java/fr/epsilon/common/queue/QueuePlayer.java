package fr.epsilon.common.queue;

import java.util.List;

public class QueuePlayer {
    private final List<String> players;
    private final String queue;

    public QueuePlayer(List<String> players, String queue) {
        this.players = players;
        this.queue = queue;
    }
}
