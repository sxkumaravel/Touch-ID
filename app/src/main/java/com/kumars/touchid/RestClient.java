package com.kumars.touchid;

import android.util.Log;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.rest.RestService;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Rest implementation for REST interface.
 *
 * @author kumars
 */
@EBean
public class RestClient {

    private static final String TAG = RestClient.class.getSimpleName();

    @RestService
    RestInterface mRestInterface;

    @AfterInject
    void setUpServices() {
        RestTemplate restTemplate = mRestInterface.getRestTemplate();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(10000);
        requestFactory.setConnectTimeout(15000);

        restTemplate.setRequestFactory(requestFactory);
    }

    public void registerUser(AppUser user, NetworkManager.NetworkCallback callback) {
        callback.onBegin();
        try {

            AppUser response = mRestInterface.registerUser(user);
            if (response != null) {
                callback.onSuccess();
            } else {
                callback.onFailure(new Exception("False"));
            }
        } catch (Exception e) {
            callback.onFailure(e);
        } finally {
            callback.onFinish();
        }
    }


    public void validateUser(Transaction transaction, String signedTransaction, NetworkManager.NetworkCallback callback) {
        callback.onBegin();
        try {
            String transactionJson = new Gson().toJson(transaction);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
            params.add("transaction", transactionJson);
            params.add("transactionSignature", signedTransaction);

            boolean status = mRestInterface.validateUser(params);
            if (status) {
                callback.onSuccess();
            } else {
                callback.onFailure(new Exception("False"));
            }
        } catch (Exception e) {
            Log.d("TAG", e.getMessage());
            callback.onFailure(e);
        } finally {
            callback.onFinish();
        }
    }
}
