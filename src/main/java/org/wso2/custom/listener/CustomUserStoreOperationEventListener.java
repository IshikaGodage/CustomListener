package org.wso2.custom.listener;

import org.wso2.carbon.identity.core.AbstractIdentityUserOperationEventListener;
import org.wso2.carbon.identity.core.util.IdentityCoreConstants;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.UserStoreManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomUserStoreOperationEventListener extends AbstractIdentityUserOperationEventListener {

    public CustomUserStoreOperationEventListener() {

        super();
    }

    @Override
    public int getExecutionOrderId() {

        int orderId = getOrderId();
        if (orderId != IdentityCoreConstants.EVENT_LISTENER_ORDER_ID) {
            return orderId;
        }
        return 193;
    }

    @Override
    public boolean doPostGetUserClaimValues(String userName, String[] claims, String profileName, Map<String, String> claimMap, UserStoreManager storeManager) throws UserStoreException {

        int tenantId = storeManager.getTenantId();  // E.g. -1234
        String domainFreeUsername = userName;


        List<String> mobileNumbers = new ArrayList<>();

        try (Connection connection = IdentityDatabaseUtil.getDBConnection(); PreparedStatement prepStmt = connection
                .prepareStatement("SELECT IDP_USER_ID FROM IDN_ASSOCIATED_ID WHERE TENANT_ID=? and USER_NAME=?")){

            prepStmt.setInt(1, tenantId);
            prepStmt.setString(2, domainFreeUsername);

            try (ResultSet resultSet = prepStmt.executeQuery()) {
                while (resultSet.next()) {
                    System.out.println(resultSet.getString(1));
                    mobileNumbers.add(resultSet.getString(1));
                }
                connection.commit();
            }
        } catch (SQLException e) {
//            throw new UserProfileException("Error occurred while retrieving federated accounts associated for " +
//                    "user: " + domainFreeUsername + " of user store domain: " + userStoreDomain + " in tenant: " +
//                    tenantId, e);
            System.out.println("Something went wrong.");
        }

        String v = null;
        for (String mobilenumber : mobileNumbers) {
            if (v == null) {
                v = mobilenumber;
            } else {
                v = v + "," + mobilenumber;
            }
        }
        String mobileClaim = "http://wso2.org/claims/mobile";
        for (String claim : claims) {
            if (claim.equals(mobileClaim)){
                claimMap.put(mobileClaim, v);
            }
        }

        return true;
    }


}
