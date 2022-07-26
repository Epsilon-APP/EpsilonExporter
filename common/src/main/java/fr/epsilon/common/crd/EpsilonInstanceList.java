package fr.epsilon.common.crd;

import com.google.gson.annotations.SerializedName;
import io.kubernetes.client.common.KubernetesListObject;
import io.kubernetes.client.openapi.models.V1ListMeta;

import java.util.ArrayList;
import java.util.List;

public class EpsilonInstanceList implements KubernetesListObject {
    public static final String SERIALIZED_NAME_API_VERSION = "apiVersion";

    public static final String SERIALIZED_NAME_API_ITEMS = "items";

    public static final String SERIALIZED_NAME_KIND = "kind";

    public static final String SERIALIZED_NAME_METADATA = "metadata";

    @SerializedName(SERIALIZED_NAME_API_VERSION)
    private String apiVersion;

    @SerializedName(SERIALIZED_NAME_API_ITEMS)
    private List<EpsilonInstance> items = new ArrayList<>();

    @SerializedName(SERIALIZED_NAME_KIND)
    private String kind;

    @SerializedName(SERIALIZED_NAME_METADATA)
    private V1ListMeta metadata;

    @Override
    public V1ListMeta getMetadata() {
        return metadata;
    }

    @Override
    public List<EpsilonInstance> getItems() {
        return items;
    }

    @Override
    public String getApiVersion() {
        return apiVersion;
    }

    @Override
    public String getKind() {
        return kind;
    }
}
