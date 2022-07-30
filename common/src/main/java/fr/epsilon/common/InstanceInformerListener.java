package fr.epsilon.common;

import fr.epsilon.common.crd.EpsilonInstanceCRD;
import fr.epsilon.common.instance.EInstance;

public interface InstanceInformerListener {
    void onInstanceUpdate(EInstance instance);
    void onInstanceRemove(EInstance instance);
}
