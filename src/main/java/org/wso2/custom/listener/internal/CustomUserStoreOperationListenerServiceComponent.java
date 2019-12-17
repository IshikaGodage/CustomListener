package org.wso2.custom.listener.internal;

import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.user.core.listener.AuthorizationManagerListener;
import org.wso2.carbon.user.core.listener.UserOperationEventListener;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.custom.listener.CustomUserStoreOperationEventListener;

/**
 * @scr.component name="org.wso2.custom.listener.internal.CustomUserStoreOperationListenerServiceComponent"
 * immediate=true
 * @scr.reference name="realm.service"
 * interface="org.wso2.carbon.user.core.service.RealmService"cardinality="1..1"
 * policy="dynamic" bind="setRealmService" unbind="unsetRealmService"
 */
public class CustomUserStoreOperationListenerServiceComponent {

    private static CustomUserStoreOperationEventListener listener = null;
    private static RealmService realmService;

    protected void activate(ComponentContext context) {

        //register the custom listeners as an OSGI service.
        context.getBundleContext().registerService(UserOperationEventListener.class, new CustomUserStoreOperationEventListener(), null);

    }

    protected void deactivate(ComponentContext context) {
    }

    protected void setRealmService(RealmService realmService) {

        CustomUserStoreOperationListenerServiceComponent.realmService = realmService;
    }

    protected void unsetRealmService(RealmService realmService) {

        CustomUserStoreOperationListenerServiceComponent.realmService = null;
    }

    public static RealmService getRealmService() {

        return realmService;
    }

}
