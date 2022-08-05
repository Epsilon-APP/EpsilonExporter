package fr.epsilon.api.informer;

import fr.epsilon.api.instance.EInstance;
import fr.epsilon.api.instance.EType;

public abstract class EInstanceInformer {
    /**
     * Get instance stored in informer
     * Some instance information may be outdated like online count
     *
     * @param name Instance name
     * @return Instance
     */
    public abstract EInstance getInstance(String name);

    /**
     * Get instances array stored in informer
     * Some template information may be outdated like online count
     *
     * @return All instances
     */
    public abstract EInstance[] getInstances();

    /**
     * Get instances array stored in informer filtered by template name
     * Some template information may be outdated like online count
     *
     * @param template Template name
     * @return All instances for specified template
     */
    public abstract EInstance[] getInstances(String template);

    /**
     * Get instances array stored in informer filtered by instance type
     * Some template information may be outdated like online count
     *
     * @param type Instance type
     * @return All instances for specified instance type
     */
    public abstract EInstance[] getInstances(EType type);

    /**
     * Register listener for watch new instance created and deleted
     *
     * @param listener Listener object
     */
    public abstract void registerListener(EInstanceInformerListener listener);
}
