package fr.epsilon.api.template;

public abstract class EResources {
    /**
     * Get minimum resource of instance
     *
     * @return Minimum resource
     */
    public abstract EResource getMinimum();

    /**
     * Get maximum resource of instance
     *
     * @return Maximum resource
     */
    public abstract EResource getMaximum();

    public static class EResource {
        private int cpu;
        private int ram;
    }
}
