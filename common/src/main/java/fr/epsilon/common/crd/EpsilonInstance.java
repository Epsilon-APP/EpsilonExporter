package fr.epsilon.common.crd;

import com.google.gson.annotations.SerializedName;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.models.V1ObjectMeta;

public class EpsilonInstance implements KubernetesObject {
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
    private EpsilonInstanceStatus status;

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

    public EpsilonInstanceStatus getStatus() {
        return status;
    }
}
