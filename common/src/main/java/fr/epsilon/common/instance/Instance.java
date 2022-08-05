package fr.epsilon.common.instance;

import com.google.gson.Gson;
import fr.epsilon.api.instance.EInstance;
import fr.epsilon.api.instance.EState;
import fr.epsilon.api.instance.EType;
import fr.epsilon.common.Epsilon;

public class Instance extends EInstance {
    private static final Gson gson = new Gson();

    private String name;
    private String template;

    private String content;

    private boolean hub;

    private EType type;
    private EState state;

    private int slots;
    private int online_count;

    private String ip;

    public Instance(String name, String template, String content, boolean hub, EType type, EState state, int slots, int online_count, String ip) {
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTemplate() {
        return template;
    }

    @Override
    public <T> T getContent(Class<T> classType) {
        return gson.fromJson(content, classType);
    }

    @Override
    public boolean isHub() {
        return hub;
    }

    @Override
    public EType getType() {
        return type;
    }

    @Override
    public EState getState() {
        return state;
    }

    @Override
    public int getSlots() {
        return slots;
    }

    @Override
    public int getOnlineCount() {
        return online_count;
    }

    @Override
    public String getLocalIp() {
        return ip;
    }

    public boolean enableInGame() {
        return Epsilon.get().instanceModule().inGameInstance(name);
    }

    @Override
    public boolean close() {
        return Epsilon.get().instanceModule().closeInstance(name);
    }
}
