package fr.epsilon.common.template;

import fr.epsilon.api.template.EResources;

public class Resources extends EResources {
    private EResource minimum;
    private EResource maximum;

    @Override
    public EResource getMinimum() {
        return minimum;
    }

    @Override
    public EResource getMaximum() {
        return maximum;
    }
}
