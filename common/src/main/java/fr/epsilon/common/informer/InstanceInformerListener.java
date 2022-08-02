package fr.epsilon.common.informer;

import fr.epsilon.common.instance.EInstance;

public interface InstanceInformerListener {
    void onInstanceUpdate(EInstance instance);
    void onInstanceRemove(EInstance instance);
}
