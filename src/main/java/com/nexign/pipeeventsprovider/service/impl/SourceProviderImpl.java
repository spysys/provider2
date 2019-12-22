package com.nexign.pipeeventsprovider.service.impl;

import com.nexign.pipeeventsprovider.components.MyRestTemplate;
import com.nexign.pipeeventsprovider.service.SourceProvider;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.PostConstruct;
import java.util.Arrays;


@Slf4j
@Service
public class SourceProviderImpl implements SourceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SourceProviderImpl.class);


    @Value("${source.host:localhost}")
    private String sourceHost;

    @Value("${source.port:8080}")
    private String sourcePort;

    @Value("${source.url.download}")
    private String sourceUrlDownload;

    private String absoluteResourceUrl;

    private static final String PROTOCOL_URL_PART = "http://";
    private static final String DELIMITER_HOST_PORT = ":";


    private final MyRestTemplate rstProxyTemplate;

    @Autowired
    public SourceProviderImpl(MyRestTemplate proxyTemplate) {
        this.rstProxyTemplate = proxyTemplate;
    }

    @PostConstruct
    void postConstruct() {
        absoluteResourceUrl = PROTOCOL_URL_PART
                .concat(sourceHost)
                .concat(DELIMITER_HOST_PORT)
                .concat(sourcePort)
                .concat(sourceUrlDownload);
    }

    @Override
    public byte[] getImage() {
        ResponseEntity<byte[]> response = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));

        HttpEntity<String> entity = new HttpEntity<String>(headers);

        try {
            response = rstProxyTemplate.getRestTemplate().exchange(absoluteResourceUrl, HttpMethod.GET, null, byte[].class);
        } catch (final HttpClientErrorException e) {
            LOG.info("request to : {} , {}", absoluteResourceUrl, e.getStatusCode());
            return null;
        }
        return response.getBody();
    }
}
