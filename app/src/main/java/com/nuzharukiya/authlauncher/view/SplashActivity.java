package com.nuzharukiya.authlauncher.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.nuzharukiya.authlauncher.R;
import com.nuzharukiya.authlauncher.db.DbAdapter;
import com.nuzharukiya.authlauncher.db.DbUtil;
import com.nuzharukiya.authlauncher.utils.HzPreferences;
import com.nuzharukiya.authlauncher.utils.UIBase;
import com.nuzharukiya.authlauncher.utils.UIComponents;

public class SplashActivity extends AppCompatActivity implements UIBase {

    // Splash time in seconds
    private static final int SPLASH_TIME = 3;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initApp();

        new IntentLauncher().start();
    }

    @Override
    public void initApp() {

        context = SplashActivity.this;
        HzPreferences.init(context);

        UIComponents uiComponents = new UIComponents(context, false);
        uiComponents.makeStatusBarTransparent();
        uiComponents.hideNavigationBar();
    }

    @Override
    public void initViews() {
        // No views to initialize here
    }

    private void startActivity(Class<?> nextClass) {
        context.startActivity(new Intent(context, nextClass));
    }

    private class IntentLauncher extends Thread {
        @Override
        /**
         * Sleep for some time and then start new activity.
         */
        public void run() {
            try {
                // Sleeping
                Thread.sleep(SPLASH_TIME * 1000);
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }

            DbAdapter.getDbAdapterInstance().open(context);
            // Check if access token is present
            if (updateTokens()) {
                // Don't prompt user to set pin if it is available
                if (!HzPreferences.getAuthPin().equalsIgnoreCase(HzPreferences.EMPTY_STRING_DEFAULT_VALUE)) {
                    HzPreferences.putUserLoggedIn(true);
                }
                startActivity(new Intent(context, AuthActivity.class));
            } else {
                // Start Login activity
                startActivity(OAuth2LoginActivity.class);
            }
            SplashActivity.this.finish();
        }

        private boolean updateTokens() {
            return DbUtil.retrieveAuthInfo();
        }
    }
}
