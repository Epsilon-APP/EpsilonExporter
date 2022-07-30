package fr.epsilon.common;

import fr.epsilon.common.crd.EpsilonInstanceCRD;
import fr.epsilon.common.crd.EpsilonInstanceCRDList;
import fr.epsilon.common.instance.EInstance;
import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.informer.cache.Lister;
import io.kubernetes.client.util.generic.GenericKubernetesApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class InstanceInformer {
    private SharedIndexInformer<EpsilonInstanceCRD> instanceInformer;

    private Lister<EpsilonInstanceCRD> instanceStore;

    private List<InstanceInformerListener> listeners;

    public InstanceInformer(String namespace, SharedInformerFactory informerFactory, GenericKubernetesApi<EpsilonInstanceCRD, EpsilonInstanceCRDList> epsilonInstanceClient) {
        this.instanceInformer = informerFactory.sharedIndexInformerFor(epsilonInstanceClient,
                EpsilonInstanceCRD.class, TimeUnit.MINUTES.toMillis(10), namespace);

        this.instanceStore = new Lister<>(instanceInformer.getIndexer());

        this.listeners = new ArrayList<>();

        instanceInformer.addEventHandlerWithResyncPeriod(new ResourceEventHandler<EpsilonInstanceCRD>() {
            @Override
            public void onAdd(EpsilonInstanceCRD instance) {}

            @Override
            public void onUpdate(EpsilonInstanceCRD oldInstance, EpsilonInstanceCRD newInstance) {
                if (newInstance.getStatus() != null) {
                    for (InstanceInformerListener listener : listeners) {
                        listener.onInstanceUpdate(newInstance.getInstance());
                    }
                }
            }

            @Override
            public void onDelete(EpsilonInstanceCRD instance, boolean deletedFinalStateUnknown) {
                for (InstanceInformerListener listener : listeners) {
                    listener.onInstanceUpdate(instance.getInstance());
                }
            }
        }, TimeUnit.MINUTES.toMillis(5));
    }

    public EInstance getInstance(String name) {
        return instanceStore.get(name).getInstance();
    }

    public List<EInstance> getInstances() {
        return instanceStore.list().stream()
                .map(EpsilonInstanceCRD::getInstance)
                .collect(Collectors.toList());
    }

    public void registerListener(InstanceInformerListener listener) {
        listeners.add(listener);
    }
}
