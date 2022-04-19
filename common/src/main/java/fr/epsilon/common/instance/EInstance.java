package fr.epsilon.common.instance;

import fr.epsilon.common.Epsilon;

public class EInstance {
    private String name;
    private String template;
    private EState state;
    private int slots;
    private int online_count;

    public String getName() {
        return name;
    }

    public String getTemplate() {
        return template;
    }

    public EState getState() {
        return state;
    }

    public int getSlots() {
        return slots;
    }

    public int getOnlineCount() {
        return online_count;
    }

    public boolean enableInGame() {
        return Epsilon.get().inGameInstance(name);
    }

    public boolean close() {
        return Epsilon.get().closeInstance(name);
    }
}
