package fr.epsilon.common.crd;

import com.google.gson.annotations.SerializedName;
import fr.epsilon.api.instance.EState;
import fr.epsilon.api.instance.EType;

public class EpsilonInstanceCRDStatus {
    public static final String SERIALIZED_NAME_IP = "ip";

    public static final String SERIALIZED_NAME_TEMPLATE = "template";

    public static final String SERIALIZED_NAME_TYPE = "t";

    public static final String SERIALIZED_NAME_HUB = "hub";

    public static final String SERIALIZED_NAME_CONTENT = "content";

    public static final String SERIALIZED_NAME_STATE = "state";

    public static final String SERIALIZED_NAME_SLOTS = "slots";

    public static final String SERIALIZED_NAME_ONLINE = "online";

    @SerializedName(SERIALIZED_NAME_IP)
    private String ip = null;

    @SerializedName(SERIALIZED_NAME_TEMPLATE)
    private String template = null;

    @SerializedName(SERIALIZED_NAME_TYPE)
    private EType type = null;

    @SerializedName(SERIALIZED_NAME_HUB)
    private Boolean hub = null;

    @SerializedName(SERIALIZED_NAME_CONTENT)
    private String content = null;

    @SerializedName(SERIALIZED_NAME_STATE)
    private EState state = null;

    @SerializedName(SERIALIZED_NAME_SLOTS)
    private Integer slots = null;

    @SerializedName(SERIALIZED_NAME_ONLINE)
    private Integer online = null;

    public String getIp() {
        return ip;
    }

    public String getTemplate() {
        return template;
    }

    public EType getType() {
        return type;
    }

    public Boolean isHub() {
        return hub;
    }

    public String getContent() {
        return content;
    }

    public EState getState() {
        return state;
    }

    public Integer getSlots() {
        return slots;
    }

    public Integer getOnlineCount() {
        return online;
    }
}
