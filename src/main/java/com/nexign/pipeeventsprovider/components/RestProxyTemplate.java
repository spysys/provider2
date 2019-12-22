package com.nexign.pipeeventsprovider.components;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(
        value="proxy.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class RestProxyTemplate implements MyRestTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestProxyTemplate.class);
    private final MyResponseErrorHandler myResponseErrorHandle;

    private RestTemplate restTemplate;

    @Value("${proxy.host}")
    private String proxyHost;

    @Value("${proxy.port}")
    private String proxyPort;

    @Value("${proxy.user}")
    private String proxyUser;

    @Value("${proxy.password}")
    private String proxyPassword;

    @Autowired
    public RestProxyTemplate(MyResponseErrorHandler myResponseErrorHandle){
        this.myResponseErrorHandle = myResponseErrorHandle;
    }

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();

        final int proxyPortNum = Integer.parseInt(proxyPort);
        final CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(proxyHost, proxyPortNum), new UsernamePasswordCredentials(proxyUser, proxyPassword));

        final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.useSystemProperties();
        clientBuilder.setProxy(new HttpHost(proxyHost, proxyPortNum));
        clientBuilder.setDefaultCredentialsProvider(credsProvider);
        clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
        final CloseableHttpClient client = clientBuilder.build();

        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(client);

        restTemplate.setRequestFactory(factory);
//        restTemplate.setErrorHandler(myResponseErrorHandle);
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}