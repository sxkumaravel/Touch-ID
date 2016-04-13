package com.kumars.touchid;

import org.androidannotations.annotations.rest.Accept;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.MediaType;
import org.androidannotations.api.rest.RestClientHeaders;
import org.androidannotations.api.rest.RestClientRootUrl;
import org.androidannotations.api.rest.RestClientSupport;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.MultiValueMap;

/**
 * @author kumars
 */
@Rest(rootUrl = "http://192.168.110.95:9097",
        converters = {FormHttpMessageConverter.class, StringHttpMessageConverter.class, GsonHttpMessageConverter.class})
public interface RestInterface extends RestClientRootUrl, RestClientSupport, RestClientHeaders {


    @Post("/user")
    AppUser registerUser(AppUser user);

    @Post("/user/login")
    @Accept(MediaType.APPLICATION_JSON)
    Boolean validateUser(MultiValueMap<String, String> params);
}
