package fr.epsilon.api.template;

import fr.epsilon.api.instance.EType;

import java.util.Map;

public abstract class ETemplate {
    /**
     * Get template name
     *
     * @return Name of template
     */
    public abstract String getName();

    /**
     * Get parent template name
     *
     * @return Name of parent template
     */
    public abstract String getParent();

    /**
     * Get template type
     *
     * @return Type of template
     */
    public abstract EType getType();

    /**
     * Get template slots
     *
     * @return Slots of template
     */
    public abstract int getSlots();

    /**
     * Get default map loaded
     *
     * @return Name of default map loaded
     */
    public abstract String getDefaultMap();

    /**
     * Get array of map name loaded
     *
     * @return Array of map name loaded
     */
    public abstract String[] getMaps();

    /**
     * Get resources template
     *
     * @return Resources of template
     */
    public abstract EResources getResources();

    /**
     * Get labels map
     *
     * @return Labels map of template
     */
    public abstract Map<String, String> getLabels();
}
