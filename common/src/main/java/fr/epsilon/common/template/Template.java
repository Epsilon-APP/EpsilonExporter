package fr.epsilon.common.template;

import fr.epsilon.api.instance.EType;
import fr.epsilon.api.template.ETemplate;

import java.util.Map;

public class Template extends ETemplate {
    private String name;
    private String parent;
    private EType type;

    private int slots;

    private String default_map;
    private String[] maps;

    private Resources resources;

    private Map<String, String> labels;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public EType getType() {
        return type;
    }

    @Override
    public int getSlots() {
        return slots;
    }

    @Override
    public String getDefaultMap() {
        return default_map;
    }

    @Override
    public String[] getMaps() {
        return maps;
    }

    @Override
    public Resources getResources() {
        return resources;
    }

    @Override
    public Map<String, String> getLabels() {
        return labels;
    }
}
