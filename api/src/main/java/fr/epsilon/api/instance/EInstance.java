package fr.epsilon.api.instance;

public abstract class EInstance {
    /**
     * Get instance name
     *
     * @return Name of instance
     */
    public abstract String getName();

    /**
     * Get template name
     *
     * @return Template name of instance
     */
    public abstract String getTemplate();

    /**
     * Get content with GSON
     *
     * @param classType Class type used for deserialization
     * @return Content object of instance
     */
    public abstract <T> T getContent(Class<T> classType);

    /**
     * Get if instance is hub
     *
     * @return Instance is hub or not
     */
    public abstract boolean isHub();

    /**
     * Get type
     *
     * @return Type of instance
     */
    public abstract EType getType();

    /**
     * Get state
     *
     * @return State of instance
     */
    public abstract EState getState();

    /**
     * Get slots
     *
     * @return Slots of instance
     */
    public abstract int getSlots();

    /**
     * Get online counts
     *
     * @return Online counts of instance
     */
    public abstract int getOnlineCount();

    /**
     * Get local ip address
     *
     * @return Ip Address of instance
     */
    public abstract String getLocalIp();

    /**
     * Enable EState.InGame
     *
     * @return Instance EState.InGame enable correctly or not
     */
    public abstract boolean enableInGame();

    /**
     * Close instance
     *
     * @return Instance close correctly or not
     */
    public abstract boolean close();
}
