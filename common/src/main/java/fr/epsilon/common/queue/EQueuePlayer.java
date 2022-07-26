package fr.epsilon.common.queue;

import java.util.List;

public class EQueuePlayer {
    private final List<String> players;
    private final String queue;

    public EQueuePlayer(List<String> players, String queue) {
        this.players = players;
        this.queue = queue;
    }
}
