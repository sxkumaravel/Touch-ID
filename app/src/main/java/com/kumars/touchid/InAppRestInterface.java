package com.kumars.touchid;

import com.google.gson.Gson;

import org.androidannotations.annotations.EBean;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kumars.
 */
@EBean
public class InAppRestInterface {

    private Map<String, AppUser> db = new HashMap<>();

    private static final String HEX_HEADER = "0x";

    public AppUser registerUser(String user) {
        AppUser appUser = new Gson().fromJson(user, AppUser.class);

        if (appUser == null) {
            return null;
        }

        if (!db.containsKey(appUser.getUsername())) {
            db.put(appUser.getUsername(), appUser);
            return appUser;
        }

        return null;
    }

    public Boolean validateUser(String transactionJson, String signedTransaction) {
        try {
            Transaction transaction = new Gson().fromJson(transactionJson, Transaction.class);

            if (transaction == null) {
                return false;
            }

            AppUser user = db.get(transaction.getUserId());

            if (user == null) {
                return false;
            }

            KeyFactory ec = KeyFactory.getInstance("EC");
            PublicKey publicKey = ec.generatePublic(new X509EncodedKeySpec(hexStringToByteArray(user.getPublicKey())));
            Signature verificationFunction = Signature.getInstance("SHA256withECDSA");
            verificationFunction.initVerify(publicKey);
            verificationFunction.update(transaction.toByteArray());

            if (verificationFunction.verify(hexStringToByteArray(signedTransaction))) {
                System.out.println("Verification succeeded.");
                return true;
            } else {
                System.out.println("Verification failed.");
            }
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] hexStringToByteArray(String value) {
        if (value == null)
            return null;

        value = value.trim();
        if (value.startsWith(HEX_HEADER))
            value = value.substring((HEX_HEADER).length());
        int len = value.length();
        if (len % 2 != 0) {
            value = "0" + value;
            len = value.length();
        }

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(value.charAt(i), 16) << 4)
                    + Character.digit(value.charAt(i + 1), 16));
        }

        return data;
    }
}
