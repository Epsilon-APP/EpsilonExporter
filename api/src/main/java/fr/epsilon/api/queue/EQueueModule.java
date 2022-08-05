package fr.epsilon.api.queue;

import java.util.List;

public abstract class EQueueModule {
    /**
     * Join queue alone
     *
     * @param username Username of player
     * @param queue    Queue name
     * @return Player queued correctly or not
     */
    public abstract boolean join(String username, String queue);

    /**
     * Join queue group
     *
     * @param usernames List of usernames in group
     * @param queue     Queue name
     * @return Players queued correctly or not
     */
    public abstract boolean join(List<String> usernames, String queue);
}
