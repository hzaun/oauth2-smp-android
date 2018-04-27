package com.nuzharukiya.authlauncher.utils;

import android.util.JsonReader;
import android.util.JsonToken;

import com.nuzharukiya.authlauncher.db.DbUtil;
import com.sap.smp.client.httpc.authflows.oauth2.AbstractOAuth2BearerTokenWebFlowConfig;

import java.io.IOException;

/**
 * Created by Nuzha Rukiya on 18/04/25.
 */
public class OAuthHelper implements
        OAuthConfig {

    AbstractOAuth2BearerTokenWebFlowConfig oAuth2BearerConfig = null;

    public String makePortInvariantUrl(String url) {
        String lcUrl = url.toLowerCase();
        boolean isHttps = false;
        if (lcUrl.startsWith("http://") || (isHttps = lcUrl.startsWith("https://"))) {
            int fromIndex = isHttps ? 8 : 7;
            int i = url.indexOf(47, fromIndex);
            if (i < 0) {
                i = url.length();
            }
            int j = url.indexOf(58, fromIndex);
            if (j >= 0 && j < i) {
                String strPort = url.substring(j + 1, i);

                try {
                    int port = Integer.parseInt(strPort);
                    if (port == (isHttps ? 443 : 80)) {
                        url = url.substring(0, j) + url.substring(i);
                    }
                } catch (NumberFormatException var8) {
                    var8.printStackTrace();
                }
            }
        }
        return url;
    }

    /**
     * Retrieves and stores the access and refresh token
     *
     * @param reader
     * @throws IOException
     */
    public void captureTokens(JsonReader reader) throws IOException {
        reader.beginObject();

        int tokenCount = 0;

        while (reader.hasNext()) {

            if (reader.peek() == JsonToken.NAME) {
                String nextName = reader.nextName();
                if (reader.peek() == JsonToken.STRING) {
                    switch (nextName) {
                        case "access_token": {
                            HzPreferences.setAccessToken(reader.nextString());
                            tokenCount++;
                        }
                        break;
                        case "refresh_token": {
                            HzPreferences.setRefreshToken(reader.nextString());
                            tokenCount++;
                        }
                        break;
                    }
                }
            }

            if (tokenCount >= 2) {
                DbUtil.updateOAuthTokens(HzPreferences.getAccessToken(), HzPreferences.getRefreshToken());
                break;
            }

            reader.skipValue();
        }
    }

    public AbstractOAuth2BearerTokenWebFlowConfig getoAuth2Config() {
        if (oAuth2BearerConfig == null) {
            return createOAuthBearerTokenConfig();
        }
        return oAuth2BearerConfig;
    }

    private AbstractOAuth2BearerTokenWebFlowConfig createOAuthBearerTokenConfig() {
        return new AbstractOAuth2BearerTokenWebFlowConfig() {
            @Override
            public String getAuthorizationEndpointUrl() {
                return AUTH_END_POINT;
            }

            @Override
            public String getRedirectUri() {
                return REDIRECT_URI;
            }

            @Override
            public String getAuthorizationCodeParamName() {
                return authCodeParam;
            }

            @Override
            public String getTokenEndpointUrl() {
                return TOKEN_END_POINT;
            }

            @Override
            public String getClientId() {
                return clientId;
            }
        };
    }
}
