package fr.epsilon.api.informer;

import fr.epsilon.api.instance.EInstance;

public interface EInstanceInformerListener {
    void onInstanceUpdate(EInstance instance);

    void onInstanceRemove(EInstance instance);
}
