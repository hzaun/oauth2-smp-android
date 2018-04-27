package com.nuzharukiya.authlauncher.utils;

/**
 * Created by Nuzha Rukiya on 18/04/25.
 */

public interface OAuthConfig {

    String serverId = "abcdef";
    String clientId = "ghijkl";

    String grantType = "authorization_code";
    String authCodeParam = "code";

    String AUTH_END_POINT = "https://oauthasservices-" + serverId + ".us2.hana.ondemand.com/oauth2/api/v1/authorize";
    String TOKEN_END_POINT = "https://oauthasservices-" + serverId + ".us2.hana.ondemand.com/oauth2/api/v1/token";
    String USER_INFO_END_POINT = "https://oauthasservices-" + serverId + ".us2.hana.ondemand.com/oauth2";
    String REDIRECT_URI = "https://oauthasservices-" + serverId + ".us2.hana.ondemand.com";
}
