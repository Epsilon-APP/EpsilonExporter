package fr.epsilon.common.template;

public class EResources {
    private EResource minimum;
    private EResource maximum;

    public EResource getMinimum() {
        return minimum;
    }

    public EResource getMaximum() {
        return maximum;
    }

    public static class EResource {
        private int cpu;
        private int ram;
    }
}
