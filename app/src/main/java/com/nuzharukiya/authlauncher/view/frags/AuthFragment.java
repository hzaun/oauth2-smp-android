package com.nuzharukiya.authlauncher.view.frags;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.nuzharukiya.authlauncher.R;
import com.nuzharukiya.authlauncher.db.DbUtil;
import com.nuzharukiya.authlauncher.utils.FingerprintHandler;
import com.nuzharukiya.authlauncher.utils.HzPreferences;
import com.nuzharukiya.authlauncher.utils.UIBase;
import com.nuzharukiya.authlauncher.utils.UIComponents;
import com.nuzharukiya.authlauncher.view.AuthActivity;
import com.nuzharukiya.authlauncher.view.DashboardActivity;
import com.nuzharukiya.authlauncher.view.FingerprintAuthActivity;

import static com.nuzharukiya.authlauncher.utils.BaseUtils.makeToast;
import static com.nuzharukiya.authlauncher.utils.Constants.AUTH_PIN_SIZE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AuthFragment extends Fragment implements UIBase, View.OnClickListener {

    private AuthActivity context;
    private View rootView;

    private TextView tvAuthPrompt;
    private AutoCompleteTextView actvAuthPin;
    private Button bNext;

    private String sAuthPin = "";

    public AuthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.frag_auth, container, false);

        initApp();
        initViews();

        return rootView;
    }

    @Override
    public void initApp() {
        context = (AuthActivity) getActivity();

        UIComponents uiComponents = new UIComponents(context, false);
        uiComponents.adjustPan();
    }

    @Override
    public void initViews() {
        tvAuthPrompt = rootView.findViewById(R.id.tvAuthPrompt);
        actvAuthPin = rootView.findViewById(R.id.actvAuthPin);
        bNext = rootView.findViewById(R.id.bNext);

        if (context.hasUserAlreadyLoggedIn()) {
            tvAuthPrompt.setVisibility(View.GONE);
        } else {
            if (context.isbConfirmAuthScreen()) {
                tvAuthPrompt.setText(R.string.auth_prompt_confirm_auth);
            } else {
                tvAuthPrompt.setText(context.getString(R.string.auth_prompt_auth, AUTH_PIN_SIZE));
            }
        }
        actvAuthPin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkAndNavigate();
                    return true;
                }
                return false;
            }
        });
        bNext.setOnClickListener(this);

        View.OnFocusChangeListener hzFCListener = new HZFocusChangeListener(actvAuthPin.getId());
        actvAuthPin.setOnFocusChangeListener(hzFCListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bNext: {
                checkAndNavigate();
            }
            break;
        }
    }

    private void checkAndNavigate() {
        sAuthPin = actvAuthPin.getText().toString();

        if (context.isbConfirmAuthScreen() || context.hasUserAlreadyLoggedIn()) {
            // Confirm Auth Screen
            if (verifyPin()) {
                FingerprintHandler handler = new FingerprintHandler(context);

                if (!context.hasUserAlreadyLoggedIn() && handler.checkFingerprintFunc()) {
                    // First time log in + Fingerprint func available
                    startActivity(FingerprintAuthActivity.class);
                } else {
                    HzPreferences.putUserLoggedIn(true);
                    startActivity(DashboardActivity.class);
                }
            }
        } else {
            // Auth Screen
            if (verifyPin()) {
                context.setAuthPin(sAuthPin);
                context.setbConfirmAuthScreen(true); // Next is confirm auth screen
            }
        }
    }

    /**
     * If user is entering for the first time,
     * Auth Screen
     * - It checks if the pin is valid,
     * Verify Auth Screen
     * - Checks if the pin matches,
     * - Stores the pin in HzPreferences
     * If the user has logged in before,
     * - Checks if the pin matches
     */
    private boolean verifyPin() {
        if (sAuthPin.isEmpty()) {
            makeToast(context, R.string.auth_enter);
        } else if (sAuthPin.length() == AUTH_PIN_SIZE) {
            if (context.isbConfirmAuthScreen()) {
                if (context.hasUserAlreadyLoggedIn()) {
                    if (HzPreferences.getAuthPin().equals(sAuthPin)) {
                        return true;
                    } else {
                        makeToast(context, R.string.auth_incorrect);
                    }
                } else {
                    // First time logging in
                    if (!context.getAuthPin().equals(sAuthPin)) {
                        makeToast(context, R.string.auth_no_match);
                    } else {
                        // Auth PIN matches
                        storeAuthPin();
                        return true;
                    }
                }
            } else {
                return true;
            }
        } else {
            makeToast(context, getString(R.string.auth_criteria_len, AUTH_PIN_SIZE));
        }

        return false;
    }

    /**
     * Stores the auth pin in the preferences
     * And also in the shared db
     */
    private void storeAuthPin() {
        // Store Auth PIN in preferences
        HzPreferences.setAuthPin(sAuthPin);
        // Store Auth PIN in shared db
        DbUtil.updateAuthPin(sAuthPin);
    }

    /**
     * Starts activity with just the class name
     */
    private void startActivity(Class<?> nextClass) {
        startActivity(new Intent(context, nextClass));
        context.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        context.finish();
    }

    /**
     * Hides the keyboard when the focus changes
     * When the button is in focus,
     * the keyboard gets hidden
     */
    private class HZFocusChangeListener implements View.OnFocusChangeListener {

        private int id = -1;

        HZFocusChangeListener(int id) {
            this.id = id;
        }

        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getId() == id && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
    }
}
