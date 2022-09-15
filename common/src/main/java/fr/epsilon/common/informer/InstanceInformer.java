package fr.epsilon.common.informer;

import fr.epsilon.api.informer.EInstanceInformer;
import fr.epsilon.api.informer.EInstanceInformerListener;
import fr.epsilon.api.instance.EType;
import fr.epsilon.common.crd.EpsilonInstanceCRD;
import fr.epsilon.common.crd.EpsilonInstanceCRDList;
import fr.epsilon.common.instance.Instance;
import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.informer.cache.Lister;
import io.kubernetes.client.util.generic.GenericKubernetesApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InstanceInformer extends EInstanceInformer {
    private SharedIndexInformer<EpsilonInstanceCRD> instanceInformer;

    private Lister<EpsilonInstanceCRD> instanceStore;

    private List<EInstanceInformerListener> listeners;

    public InstanceInformer(String namespace, SharedInformerFactory informerFactory, GenericKubernetesApi<EpsilonInstanceCRD, EpsilonInstanceCRDList> epsilonInstanceClient) {
        this.instanceInformer = informerFactory.sharedIndexInformerFor(epsilonInstanceClient,
                EpsilonInstanceCRD.class, TimeUnit.MINUTES.toMillis(10), namespace);

        this.instanceStore = new Lister<>(instanceInformer.getIndexer());

        this.listeners = new ArrayList<>();

        instanceInformer.addEventHandlerWithResyncPeriod(new ResourceEventHandler<EpsilonInstanceCRD>() {
            @Override
            public void onAdd(EpsilonInstanceCRD instance) {
            }

            @Override
            public void onUpdate(EpsilonInstanceCRD oldInstance, EpsilonInstanceCRD newInstance) {
                if (newInstance.getStatus() != null) {
                    for (EInstanceInformerListener listener : listeners) {
                        listener.onInstanceUpdate(newInstance.getInstance());
                    }
                }
            }

            @Override
            public void onDelete(EpsilonInstanceCRD instance, boolean deletedFinalStateUnknown) {
                for (EInstanceInformerListener listener : listeners) {
                    listener.onInstanceRemove(instance.getInstance());
                }
            }
        }, TimeUnit.MINUTES.toMillis(5));
    }

    @Override
    public Instance getInstance(String name) {
        return instanceStore.get(name).getInstance();
    }

    @Override
    public Instance[] getInstances() {
        return instanceStore.list().stream()
                .map(EpsilonInstanceCRD::getInstance)
                .toArray(Instance[]::new);
    }

    @Override
    public Instance[] getInstances(String template) {
        return Arrays.stream(getInstances())
                .filter(instance -> instance.getTemplate().equals(template))
                .toArray(Instance[]::new);
    }

    @Override
    public Instance[] getInstances(EType type) {
        return Arrays.stream(getInstances())
                .filter(instance -> instance.getType() == type)
                .toArray(Instance[]::new);
    }

    @Override
    public void registerListener(EInstanceInformerListener listener) {
        listeners.add(listener);
    }
}
