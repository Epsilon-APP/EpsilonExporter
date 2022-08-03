package fr.epsilon.common.instance;

import com.google.gson.Gson;
import fr.epsilon.common.Epsilon;

public class EInstance {
    private static final Gson gson = new Gson();

    private String name;
    private String template;

    private Object content;

    private boolean hub;

    private EType type;
    private EState state;

    private int slots;
    private int online_count;

    private String ip;

    public EInstance(String name, String template, Object content, boolean hub, EType type, EState state, int slots, int online_count, String ip) {
        this.name = name;
        this.template = template;

        this.content = content;

        this.hub = hub;

        this.type = type;
        this.state = state;

        this.slots = slots;
        this.online_count = online_count;

        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public String getTemplate() {
        return template;
    }

    public <T> T getContent(Class<T> classType) {
        return gson.fromJson(gson.toJson(content), classType);
    }

    public boolean isHub() {
        return hub;
    }

    public EType getType() {
        return type;
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

    public String getIp() {
        return ip;
    }

    public boolean enableInGame() {
        return Epsilon.get().instanceModule().inGameInstance(name);
    }

    public boolean close() {
        return Epsilon.get().instanceModule().closeInstance(name);
    }
}
