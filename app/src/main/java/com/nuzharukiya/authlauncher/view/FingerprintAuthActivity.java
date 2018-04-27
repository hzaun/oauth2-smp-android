package com.nuzharukiya.authlauncher.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nuzharukiya.authlauncher.R;
import com.nuzharukiya.authlauncher.utils.Constants;
import com.nuzharukiya.authlauncher.utils.FingerprintHandler;
import com.nuzharukiya.authlauncher.utils.HzPreferences;
import com.nuzharukiya.authlauncher.utils.UIBase;
import com.nuzharukiya.authlauncher.utils.UIComponents;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static com.nuzharukiya.authlauncher.utils.Constants.FROM_ACTIVITY;
import static com.nuzharukiya.authlauncher.utils.Constants.HIDE_ICON;

public class FingerprintAuthActivity extends AppCompatActivity implements
        UIBase,
        View.OnClickListener {

    // Declare a string variable for the key we’re going to use in our fingerprint authentication
    private static final String KEY_NAME = "hzKey";
    private Context context;
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private FingerprintHandler handler;

    private boolean hasUserAlreadyLoggedIn = false;

    private Button bNotNow, bEnable;
    private TextView tvFaPrompt;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_auth);

        initApp();
        initViews();

        handler = new FingerprintHandler(this);

        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        //Check whether the user has granted your app the USE_FINGERPRINT permission//
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            // If your app doesn't have this permission, then display the following text//
            ((Activity) context).requestPermissions(
                    new String[]{Manifest.permission.USE_FINGERPRINT},
                    Constants.MY_PERMISSIONS_USE_FINGERPRINT);
        } else {
            scanFingerprint();
        }
    }

    @Override
    public void initApp() {
        this.context = FingerprintAuthActivity.this;

        hasUserAlreadyLoggedIn = HzPreferences.isUserLoggedIn();

        UIComponents uiComponents = new UIComponents(context, true);

        uiComponents.setToolbarItems(HIDE_ICON, R.string.fingerprint_authentication, (hasUserAlreadyLoggedIn ? R.drawable.ic_clear : HIDE_ICON));
        if (hasUserAlreadyLoggedIn) {
            Intent authIntent = new Intent(context, AuthActivity.class);
            authIntent.putExtra(FROM_ACTIVITY, getString(R.string.fingerprint_authentication));
            uiComponents.onClickEndIcon(authIntent);
        }
    }

    @Override
    public void initViews() {
        bNotNow = findViewById(R.id.bNotNow);
        bEnable = findViewById(R.id.bEnable);

        if (hasUserAlreadyLoggedIn) {
            bNotNow.setVisibility(View.GONE);
            bEnable.setVisibility(View.GONE);

            tvFaPrompt = findViewById(R.id.tvFaPrompt);

            tvFaPrompt.setText(getString(R.string.fa_instructions));

        } else {
            // First time logging in
            bNotNow.setOnClickListener(this);
            bEnable.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bNotNow: {
                HzPreferences.putFingerprintEnabled(false);
            }
            break;
            case R.id.bEnable: {
                HzPreferences.putFingerprintEnabled(true);
            }
            break;
        }
        goToDashboard();
    }

    private void goToDashboard() {
        HzPreferences.putUserLoggedIn(true);

        startActivity(new Intent(context, DashboardActivity.class));
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void scanFingerprint() {
        //Check that the lockscreen is secured//
        if (!keyguardManager.isKeyguardSecure()) {
            // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text
            Toast.makeText(context, getString(R.string.fa_lockscreen_not_secured), Toast.LENGTH_SHORT).show();
        } else {
            try {
                generateKey();
            } catch (FingerprintException e) {
                e.printStackTrace();
            }

            if (initCipher()) {
                //If the cipher is initialized successfully, then create a CryptoObject instance//
                cryptoObject = new FingerprintManager.CryptoObject(cipher);

                // Here, I’m referencing the FingerprintHandler class, which will be responsible
                // for starting the authentication process (via the startAuth method) and processing the authentication process events//
                handler.startAuth(fingerprintManager, cryptoObject);
            }
        }
    }

    //Create the generateKey method that we’ll use to gain access to the Android keystore and generate the encryption key//
    private void generateKey() throws FingerprintException {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            //Generate the key//
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            //Initialize an empty KeyStore//
            keyStore.load(null);

            //Initialize the KeyGenerator//
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyGenerator.init(new
                        //Specify the operation(s) this key can be used for//
                        KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                        //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                                KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
            }

            //Generate the key//
            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    //Create a new method that we’ll use to initialize our cipher//
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean initCipher() {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //Return true if the cipher has been initialized successfully//
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {

            //Return false if cipher initialization failed//
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_USE_FINGERPRINT: {
                // If request is cancelled, the result arrays are empty.

                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context,
                                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    scanFingerprint();
                }
            }
        }
    }

    private class FingerprintException extends Exception {
        FingerprintException(Exception e) {
            super(e);
        }
    }
}
