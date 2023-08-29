package fr.epsilon.api.instance;

import java.util.concurrent.CompletableFuture;

public abstract class EInstanceModule {
    /**
     * Get instance from CompletableFuture
     *
     * @param name Instance name
     * @return CompletableFuture of instance
     */
    public abstract CompletableFuture<? extends EInstance> getInstance(String name);

    /**
     * Get instances array from CompletableFuture
     *
     * @return CompletableFuture of all instances
     */
    public abstract CompletableFuture<? extends EInstance[]> getInstances();

    /**
     * Get instances array filtered by template name from CompletableFuture
     *
     * @param template Template name
     * @return CompletableFuture of all instances for specified template
     */
    public abstract CompletableFuture<? extends EInstance[]> getInstances(String template);

    /**
     * Get instances arrays filtered by instance type from CompletableFuture
     *
     * @param type Instance type
     * @return CompletableFuture of all instances for specified instance type
     */
    public abstract CompletableFuture<? extends EInstance[]> getInstances(EType type);

    /**
     * Open instance with template name
     *
     * @param template Template name
     * @return Instance or null if not opened
     */
    public abstract EInstance openInstance(String template);

    /**
     * Open instance with template name
     *
     * @param template Template name
     * @param content  Content field of instance
     * @return Instance or null if not opened
     */
    public abstract <T> EInstance openInstance(String template, T content);

    /**
     * Close instance
     *
     * @param name Instance name
     * @return Instance closed correctly or not
     */
    public abstract boolean closeInstance(String name);

    /**
     * Enable EState.InGame on instance
     *
     * @param name Instance name
     * @return Instance EState.InGame enabled correctly
     */
    public abstract boolean inGameInstance(String name);
}
