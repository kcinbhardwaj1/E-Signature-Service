package com.rgp.de.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth;
import com.rgp.de.config.DSConfiguration;

/**
 * This is an token base class to be extended to show functionality example.
 * its has a apiClient member as a constructor argument for later usage in API calls.
 */
public class TokenBase {

	private Logger logger = LoggerFactory.getLogger(TokenBase.class);
    private static final long TOKEN_EXPIRATION_IN_SECONDS = 3600;
    private static final long TOKEN_REPLACEMENT_IN_MILLISECONDS = 10 * 60 * 1000;

    private static OAuth.Account _account;
    private static long expiresIn;
    private static String _token = null;
    protected final ApiClient apiClient;

    public static String getAccountId() {
        return _account.getAccountId();
    };


    public TokenBase(ApiClient apiClient) throws IOException {
        this.apiClient =  apiClient;
    }

    public void checkToken() throws IOException, ApiException {
        if(_token == null
                || (System.currentTimeMillis() + TOKEN_REPLACEMENT_IN_MILLISECONDS) > this.expiresIn) {
            updateToken();
        }
    }

    private void updateToken() throws IOException, ApiException {
    	logger.info("\nFetching an access token via JWT grant...");

        java.util.List<String> scopes = new ArrayList<String>();
        // Only signature scope is needed. Impersonation scope is implied.
        scopes.add(OAuth.Scope_SIGNATURE);
        String privateKey = DSConfiguration.PRIVATE_KEY.replace("\\n","\n");
        byte[] privateKeyBytes = privateKey.getBytes();
        apiClient.setOAuthBasePath(DSConfiguration.DS_AUTH_SERVER);

        OAuth.OAuthToken oAuthToken = apiClient.requestJWTUserToken (
        		DSConfiguration.CLIENT_ID,
                DSConfiguration.IMPERSONATED_USER_GUID,
                scopes,
                privateKeyBytes,
                TOKEN_EXPIRATION_IN_SECONDS);
        apiClient.setAccessToken(oAuthToken.getAccessToken(), oAuthToken.getExpiresIn());
        logger.info("Token generation Done. Continuing...\n");

        if(_account == null)
            _account = this.getAccountInfo(apiClient);
        // default or configured account id.
        if( _account != null && _account.getBaseUri() != null)
            apiClient.setBasePath(_account.getBaseUri() + "/restapi");

        _token = apiClient.getAccessToken();
        logger.info("Token : {}",_token);
        
        expiresIn = System.currentTimeMillis() + (oAuthToken.getExpiresIn() * 1000);
        logger.info("token will expire in: {}" ,expiresIn);
    }

    private OAuth.Account getAccountInfo(ApiClient client) throws ApiException {
        OAuth.UserInfo userInfo = client.getUserInfo(client.getAccessToken());
        OAuth.Account accountInfo = null;

            List<OAuth.Account> accounts = userInfo.getAccounts();

            OAuth.Account acct  = this.find(accounts, member -> (member.getIsDefault().equals("true")));

            if (acct != null) return acct;

            acct = this.find(accounts, member -> (member.getAccountId().equals( DSConfiguration.TARGET_ACCOUNT_ID)));

            if (acct != null) return acct;


        return accountInfo;
    }

    private OAuth.Account find(List<OAuth.Account> accounts, ICondition<OAuth.Account> criteria) {
        for (OAuth.Account acct: accounts) {
            if(criteria.test(acct)){
                return acct;
            }
        }
        return null;
    }

    interface ICondition<T> {
        boolean test(T member);
    }
}
