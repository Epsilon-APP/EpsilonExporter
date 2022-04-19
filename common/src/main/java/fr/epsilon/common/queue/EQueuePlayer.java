package fr.epsilon.common.queue;

import java.util.List;

public class EQueuePlayer {
    private List<String> players;
    private String queue;

    public EQueuePlayer(List<String> players, String queue) {
        this.players = players;
        this.queue = queue;
    }
}
