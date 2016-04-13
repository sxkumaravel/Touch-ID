package com.kumars.touchid;

import android.support.annotation.NonNull;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.security.PublicKey;

/**
 * @author kumars on 4/6/16.
 */
@EBean(scope = EBean.Scope.Singleton)
public class NetworkManager {

    private static final String HEXES = "0123456789ABCDEF";

    @Bean
    RestClient mRestClient;

    @Background
    public void registerUser(String email, String password, PublicKey publicKey, @NonNull NetworkCallback callback) {
        AppUser user = new AppUser(email, password, byteArrayToHexString(publicKey.getEncoded()));
        mRestClient.registerUser(user, callback);
    }

    @Background
    public void validateUser(Transaction transaction, byte[] signedTransaction, @NonNull NetworkCallback callback) {
        mRestClient.validateUser(transaction, byteArrayToHexString(signedTransaction), callback);
    }

    public static String byteArrayToHexString(byte[] value) {
        if (value == null)
            return null;

        final StringBuilder hex = new StringBuilder(2 * value.length);
        for (final byte b : value) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    public interface NetworkCallback {
        void onBegin();

        void onFinish();

        void onSuccess();

        void onFailure(Exception e);
    }
}
