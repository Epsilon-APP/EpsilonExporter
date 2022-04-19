package fr.epsilon.common.template;

import java.util.Map;

public class ETemplate {
    private String name;
    private String parent;
    private String type;

    private int slots;

    private String default_map;
    private String[] maps;

    private EResources resources;

    private Map<String, String> labels;

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }

    public String getType() {
        return type;
    }

    public int getSlots() {
        return slots;
    }

    public String getDefaultMap() {
        return default_map;
    }

    public String[] getMaps() {
        return maps;
    }

    public EResources getResources() {
        return resources;
    }

    public Map<String, String> getLabels() {
        return labels;
    }
}
