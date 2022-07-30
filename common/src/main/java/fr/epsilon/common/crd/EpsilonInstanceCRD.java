package fr.epsilon.common.crd;

import com.google.gson.annotations.SerializedName;
import fr.epsilon.common.instance.EInstance;
import fr.epsilon.common.instance.EState;
import fr.epsilon.common.instance.EType;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.models.V1ObjectMeta;

public class EpsilonInstanceCRD implements KubernetesObject {
    public static final String SERIALIZED_NAME_API_VERSION = "apiVersion";

    public static final String SERIALIZED_NAME_KIND = "kind";

    public static final String SERIALIZED_NAME_METADATA = "metadata";

    public static final String SERIALIZED_NAME_STATUS = "status";

    @SerializedName(SERIALIZED_NAME_API_VERSION)
    private String apiVersion;

    @SerializedName(SERIALIZED_NAME_KIND)
    private String kind;

    @SerializedName(SERIALIZED_NAME_METADATA)
    private V1ObjectMeta metadata;

    @SerializedName(SERIALIZED_NAME_STATUS)
    private EpsilonInstanceCRDStatus status;

    @Override
    public V1ObjectMeta getMetadata() {
        return metadata;
    }

    @Override
    public String getApiVersion() {
        return apiVersion;
    }

    @Override
    public String getKind() {
        return kind;
    }

    public String getName() {
        return metadata.getName();
    }

    public EpsilonInstanceCRDStatus getStatus() {
        return status;
    }

    public EInstance getInstance() {
        if (getStatus() != null) {
            String template = getStatus().getTemplate();

            String content = getStatus().getContent();

            boolean hub = getStatus().isHub();

            EType type = getStatus().getType();
            EState state = getStatus().getState();

            int slots = getStatus().getSlots();
            int onlineCount = getStatus().getOnlineCount();

            String ip = getStatus().getIp();

            return new EInstance(getName(), template, content, hub, type, state, slots, onlineCount, ip);
        }

        return null;
    }
}
