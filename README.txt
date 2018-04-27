OAuth 2.0 login using SMP in Android

// OAuth 2.0

**Change the serverId and clientId in OAuthConfig.**

The OAuth2LoginActivity creates a webView::

The user is first taken to the auth endpoint url, where the user logs in as they would with SAML.
If successful, the response url is parsed, and if it contains the auth "code", it is retrieved from the url.
Then, a post request is created with this auth code, and its response parsed to get the access and refresh token.

// SharedUserId

The app saves the auth pin, access and refresh token in the db.
With a shared user id this token can be used between multiple apps ( with the same IDP ).

This app (host) must be installed before the receiver apps.

In the receiver app, use a sharedContext to access this apps db.

    public static Context createSharedContext(Context context) {
        Context sharedContext = null;
        try {
            sharedContext = context.createPackageContext("com.nuzharukiya.authlauncher", Context.CONTEXT_INCLUDE_CODE);
            if (sharedContext == null) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sharedContext;
    }

    ...

    DbAdapter.getDbAdapterInstance().open(sharedContext);

// IMP

* This only works with SP15.

// Helpful links::

-https://help.sap.com/saphelp_nw73ehp1/helpdata/en/75/73ffc0ae444443a23b9e661d77d637/content.htm?no_cache=true
-https://help.sap.com/doc/saphelp_smp305sdk/3.0.5/en-US/81/cd5e7f80d71014964eb0f4e4028888/frameset.htm

-https://www.sap.com/developer/tutorials/hcpdo-android-sdk-setup.html
-https://www.sap.com/india/developer/tutorials/fiori-ios-scpms-saml-oauth.html
-https://help.sap.com/viewer/d40c4b7ba5d54511aaa4908fd0a18d30/Cloud%20Edition/en-US/1a7ba7119c2345b6a9b167fc023a6a5c.html?q=oauth%202.0%20android