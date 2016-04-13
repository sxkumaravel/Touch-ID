package com.kumars.touchid;

import android.app.KeyguardManager;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.springframework.web.client.HttpStatusCodeException;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author kumars on 4/6/16.
 */
@EActivity(R.layout.activity_register)
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    @Bean
    NetworkManager mNetworkManager;

    @ViewById(R.id.edit_text_email)
    EditText mEmailET;

    @ViewById(R.id.key_alias)
    EditText mPassword;

    @Pref
    Preference_ mPreference;

    @ViewById(R.id.button_id)
    Button mRegisterButton;


    @AfterViews
    protected void afterViews() {
        FingerprintManager fingerprintManager = getSystemService(FingerprintManager.class);

        mRegisterButton.setEnabled(false);

        if (!fingerprintManager.isHardwareDetected()) {
            showToast("No finger print hardware detected");
            return;
        }

        if (!getSystemService(KeyguardManager.class).isKeyguardSecure()) {
            showToast("Secure lock screen hasn't set up.\nGo to 'Settings -> Security -> Fingerprint' to set up a fingerprint");
            return;
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {
            showToast("Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint");
            return;
        }

        mRegisterButton.setEnabled(true);

    }

    @Click(R.id.button_id)
    protected void onRegisterClicked() {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }

        if (keyPairGenerator == null) {
            showToast("Filed ot get the instance of KeyPairGenerator instance");
            return;
        }
        String email = mEmailET.getText().toString();
        if (email.length() < 5) {
            showToast("Enter a valid email");
            return;
        }

        String password = mPassword.getText().toString();
        if (password.length() < 5) {
            showToast("Enter a valid password");
            return;
        }

        KeyGenParameterSpec builder = new KeyGenParameterSpec.Builder(email, KeyProperties.PURPOSE_SIGN)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                .setUserAuthenticationRequired(true)
                .build();

        try {
            keyPairGenerator.initialize(builder);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        keyPairGenerator.generateKeyPair();

        PublicKey verificationKey = null;

        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            PublicKey publicKey = keyStore.getCertificate(email).getPublicKey();

            KeyFactory factory = KeyFactory.getInstance(publicKey.getAlgorithm());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey.getEncoded());
            verificationKey = factory.generatePublic(spec);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (verificationKey == null) {
            showToast("Public key is null");
            return;
        }

        registerUser(email, password, verificationKey);
    }

    private void registerUser(final String email, String password, PublicKey publicKey) {
        mNetworkManager.registerUser(email, password, publicKey, new NetworkManager.NetworkCallback() {
            @Override
            public void onBegin() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onSuccess() {
                mPreference.userEmail().put(email);
                showToast("Registration Success");
            }

            @Override
            public void onFailure(Exception e) {

                if (e instanceof HttpStatusCodeException) {
                    int errorCode = ((HttpStatusCodeException) e).getStatusCode().value();
                    if (errorCode == 400) {
                        showToast("User Profile Already Exists");
                    }
                }
                mPreference.userEmail().put("");
                showToast("Registration Failed");
            }
        });
    }

    @UiThread
    protected void showToast(String string) {
        Log.d(TAG, string);
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }
}
