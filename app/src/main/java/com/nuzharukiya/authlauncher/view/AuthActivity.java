package com.nuzharukiya.authlauncher.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.nuzharukiya.authlauncher.R;
import com.nuzharukiya.authlauncher.utils.Constants;
import com.nuzharukiya.authlauncher.utils.HzPreferences;
import com.nuzharukiya.authlauncher.utils.UIBase;
import com.nuzharukiya.authlauncher.utils.UIComponents;
import com.nuzharukiya.authlauncher.view.frags.AuthFragment;

public class AuthActivity extends AppCompatActivity implements UIBase {

    private Context context;

    private String FROM = "";

    private String authPin = "";
    private boolean hasUserAlreadyLoggedIn = false;
    private boolean bConfirmAuthScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        hasUserAlreadyLoggedIn = HzPreferences.isUserLoggedIn();

        initApp();
        initViews();
    }

    @Override
    public void initApp() {
        context = AuthActivity.this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            FROM = bundle.getString(Constants.FROM_ACTIVITY);
        }

        UIComponents uiComponents = new UIComponents(context, true);
        uiComponents.setToolbarItems(hasUserAlreadyLoggedIn ? R.string.auth_enter : R.string.activity_auth);
        uiComponents.adjustPan();
    }

    @Override
    public void initViews() {
        if (getFROM().equalsIgnoreCase(context.getString(R.string.fingerprint_authentication))) {
            setbConfirmAuthScreen(true);
        } else {
            goToFragment(new AuthFragment());
        }
    }

    private void goToFragment(Fragment nextFragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
//                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                .add(R.id.flContainer, nextFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean hasUserAlreadyLoggedIn() {
        return hasUserAlreadyLoggedIn;
    }

    public boolean isbConfirmAuthScreen() {
        return bConfirmAuthScreen;
    }

    public void setbConfirmAuthScreen(boolean bConfirmAuthScreen) {
        this.bConfirmAuthScreen = bConfirmAuthScreen;

        if (bConfirmAuthScreen) {
            goToFragment(new AuthFragment());
        }
    }

    public String getAuthPin() {
        return authPin;
    }

    public void setAuthPin(String authPin) {
        this.authPin = authPin;
    }

    public String getFROM() {
        return FROM;
    }
}
