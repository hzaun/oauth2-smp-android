package com.nuzharukiya.authlauncher.view;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nuzharukiya.authlauncher.utils.OAuthConfig;
import com.nuzharukiya.authlauncher.utils.OAuthHelper;
import com.nuzharukiya.authlauncher.utils.UIBase;
import com.sap.smp.client.httpc.HttpConversationManager;
import com.sap.smp.client.httpc.HttpMethod;
import com.sap.smp.client.httpc.IHttpConversation;
import com.sap.smp.client.httpc.events.IReceiveEvent;
import com.sap.smp.client.httpc.events.ISendEvent;
import com.sap.smp.client.httpc.events.ITransmitEvent;
import com.sap.smp.client.httpc.listeners.IRequestListener;
import com.sap.smp.client.httpc.listeners.IResponseListener;
import com.sap.smp.client.httpc.utils.EmptyFlowListener;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class OAuth2LoginActivity extends AppCompatActivity implements
        UIBase {

    private Context context;
    private OAuthHelper oAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initApp();
        initViews();
    }

    private WebView createWebView() {

        final String redirectUri = oAuthHelper.makePortInvariantUrl(oAuthHelper.getoAuth2Config().getRedirectUri());
        String authorizationCodeParamName = oAuthHelper.getoAuth2Config().getAuthorizationCodeParamName();
        final String codeParam1 = "?" + authorizationCodeParamName + "=";
        final String codeParam2 = "&" + authorizationCodeParamName + "=";
        final int codeParamLength = authorizationCodeParamName.length() + 2;
        StrictMode.ThreadPolicy policy = (new StrictMode.ThreadPolicy.Builder()).permitAll().build();
        StrictMode.setThreadPolicy(policy);
        WebView webView = new WebView(this);
        webView.setLongClickable(true);
        WebSettings settings = webView.getSettings();
        settings.setAppCacheEnabled(false);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSaveFormData(false);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                int i = url.indexOf(codeParam1);
                if (i < 0) {
                    i = url.indexOf(codeParam2);
                }

                if (i > 0 && oAuthHelper.makePortInvariantUrl(url).startsWith(redirectUri)) {
                    i += codeParamLength;
                    int j = url.indexOf("&", i);
                    int length = url.length();
                    if (j < 0) {
                        j = length;
                    }

                    final String authorizationCode = j <= length ? url.substring(i, j) : null;
                    if (authorizationCode != null) {
                        HttpConversationManager manager = new HttpConversationManager(context);

                        IHttpConversation conv;
                        try {
                            conv = manager.create(new URL(oAuthHelper.getoAuth2Config().getTokenEndpointUrl()));
                        } catch (MalformedURLException var10) {
                            throw new IllegalStateException("Token endpoint URL was unparseable", var10);
                        }

                        conv.setMethod(HttpMethod.POST);
                        conv.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                        conv.setRequestListener(new IRequestListener() {
                            public Object onRequestHeaderSending(ISendEvent event) {
                                return null;
                            }

                            public Object onRequestBodySending(ITransmitEvent event) throws IOException {
                                OutputStreamWriter w = event.getWriter();
                                w.write("grant_type=");
                                w.write(OAuthConfig.grantType);
                                w.write("&code=");
                                w.write(URLEncoder.encode(authorizationCode, "UTF-8"));
                                w.write("&redirect_uri=");
                                w.write(URLEncoder.encode(redirectUri, "UTF-8"));
                                w.write("&client_id=");
                                w.write(URLEncoder.encode(oAuthHelper.getoAuth2Config().getClientId(), "UTF-8"));
                                return null;
                            }
                        });
                        conv.setResponseListener(new IResponseListener() {
                            public void onResponseReceived(IReceiveEvent event) throws IOException {
                                Reader r = event.getReader();
                                if (r != null && event.getResponseStatusCode() < 400) {
                                    oAuthHelper.captureTokens(new JsonReader(r));
                                }
                            }
                        });
                        conv.setFlowListener(new EmptyFlowListener() {
                            public void onCompletion() {
                                Intent authIntent = new Intent(context, AuthActivity.class);
                                startActivity(authIntent);
                            }
                        });
                        conv.start();
                    }

                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }
        });
        webView.setScrollContainer(true);
        webView.setWebChromeClient(new WebChromeClient());

        try {
            webView.loadUrl(new URL(
                    oAuthHelper.getoAuth2Config().getAuthorizationEndpointUrl())
                    + "?response_type=" + OAuthConfig.authCodeParam
                    + "&client_id=" + OAuthConfig.clientId
                    + "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8"));
        } catch (UnsupportedEncodingException | MalformedURLException var12) {
            throw new IllegalStateException(var12);
        }

        return webView;
    }

    @Override
    public void initApp() {
        context = this;
        oAuthHelper = new OAuthHelper();
    }

    @Override
    public void initViews() {
        setContentView(createWebView());
    }
}
