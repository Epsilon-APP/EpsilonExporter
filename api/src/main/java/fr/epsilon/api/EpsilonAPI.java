package fr.epsilon.api;

import fr.epsilon.api.informer.EInstanceInformer;
import fr.epsilon.api.instance.EInstance;
import fr.epsilon.api.instance.EInstanceModule;
import fr.epsilon.api.queue.EQueueModule;
import fr.epsilon.api.template.ETemplate;

import java.util.concurrent.CompletableFuture;

public abstract class EpsilonAPI {
    private static EpsilonAPI singleton;

    /**
     * Get EpsilonAPI from singleton
     *
     * @return Epsilon API
     */
    public static EpsilonAPI get() {
        return singleton;
    }

    /**
     * Run instance informer
     * This allows instances to be cached and retrieved synchronously.
     * However, with this technique certain fields of the instance are not updated in real time.
     * Such as the number of players online.
     *
     * @return Instance informer for get instance from store
     */
    public abstract EInstanceInformer runInstanceInformer();

    /**
     * Get current instance name
     *
     * @return Instance name of current instance
     */
    public abstract String name();

    /**
     * Get current template
     *
     * @return Template of current instance
     */
    public abstract ETemplate template();

    /**
     * Get current instance from CompletableFuture
     *
     * @return CompletableFuture of current instance
     */
    public abstract CompletableFuture<? extends EInstance> instance();

    /**
     * InstanceModule for interact with Epsilon instance
     *
     * @return InstanceModule
     */
    public abstract EInstanceModule instanceModule();

    /**
     * QueueModule for interact with Epsilon queue
     *
     * @return QueueModule
     */
    public abstract EQueueModule queueModule();

    /**
     * Get if server is reachable
     *
     * @return boolean
     */
    public abstract boolean isReachable();

    public EpsilonAPI() {
        singleton = this;
    }
}
