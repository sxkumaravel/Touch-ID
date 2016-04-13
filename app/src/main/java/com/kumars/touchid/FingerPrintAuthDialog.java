package com.kumars.touchid;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

/**
 * @author kumars on 4/6/16.
 */

@EFragment(R.layout.fragment_finger_print)
public class FingerPrintAuthDialog extends DialogFragment {

    private CancellationSignal mCancellationSignal;

    @ViewById(R.id.fingerprint_status)
    TextView mAuthStatus;

    @Pref
    Preference_ mPreference;

    @Bean
    NetworkManager mNetworkManager;

    @AfterViews
    protected void afterViews() {
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);

        onValidate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().setTitle(getString(R.string.sign_in));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
        }
        super.onDismiss(dialog);
    }

    @UiThread
    protected void updateUi(String status) {
        if (getDialog() != null) {
            mAuthStatus.setText(status);
        }
    }

    protected void onValidate() {

        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");

            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            PrivateKey key = (PrivateKey) keyStore.getKey(mPreference.userEmail().get(), null);
            signature.initSign(key);

            FingerprintManager.CryptoObject cryptObject = new FingerprintManager.CryptoObject(signature);

            mCancellationSignal = new CancellationSignal();

            FingerprintManager fingerprintManager = getContext().getSystemService(FingerprintManager.class);

            fingerprintManager.authenticate(cryptObject, mCancellationSignal, 0, new FingerPrintCallback(), null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private class FingerPrintCallback extends FingerprintManager.AuthenticationCallback {

        public FingerPrintCallback() {
            super();
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            Signature signature = result.getCryptoObject().getSignature();

            Transaction transaction = new Transaction(mPreference.userEmail().get(), new SecureRandom().nextLong());

            try {
                signature.update(transaction.toByteArray());
                byte[] signedTransaction = signature.sign();
                // Send the transaction and signedTransaction to the dummy backend
                validateUser(transaction, signedTransaction);

            } catch (SignatureException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
        }
    }

    private void validateUser(Transaction transaction, byte[] signedTransaction) {
        mNetworkManager.validateUser(transaction, signedTransaction, new NetworkManager.NetworkCallback() {
            @Override
            public void onBegin() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onSuccess() {
                updateUi("Validation Success");
            }

            @Override
            public void onFailure(Exception e) {
                updateUi("Validation Failure");
            }
        });
    }
}
