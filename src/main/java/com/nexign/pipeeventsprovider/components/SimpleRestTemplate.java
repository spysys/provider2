package com.nexign.pipeeventsprovider.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(
        value="proxy.enabled",
        havingValue = "false",
        matchIfMissing = true)
public class SimpleRestTemplate implements MyRestTemplate {

    MyResponseErrorHandler myResponseErrorHandler;
    private RestTemplate restTemplate;

    @Autowired
    public SimpleRestTemplate(MyResponseErrorHandler myResponseErrorHandler){
        this.myResponseErrorHandler = myResponseErrorHandler;
    }

    @PostConstruct
    void init(){
        restTemplate = new RestTemplate();
//        restTemplate.setErrorHandler(myResponseErrorHandler);
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

    }

    @Override
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
