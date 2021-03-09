package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

/**
 * Client code does not require any test coverage
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientImpl implements Client {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Environment env;

    @Override
    public void pushData(DataEnvelope dataEnvelope) {
        log.info("Pushing data {} to {}", dataEnvelope.getDataHeader().getName(), env.getProperty("uri.pushdata"));
        boolean result = restTemplate.postForObject( env.getProperty("uri.pushdata"), dataEnvelope, Boolean.class);
        log.info("Block saved on server: "+ result);
    }

    @Override
    public List<DataEnvelope> getData(String blockType) {
        log.info("Query for data with header block type {}", blockType);
        UriTemplate URI_GETDATA = new UriTemplate(env.getProperty("uri.getdata"));
        ResponseEntity<List<DataEnvelope>> response = restTemplate.exchange(URI_GETDATA.expand(blockType)
                , HttpMethod.GET, null, new ParameterizedTypeReference<List<DataEnvelope>>() {});
        return response.getBody();
    }

    @Override
    public boolean updateData(String blockName, String newBlockType) {
        log.info("Updating blocktype to {} for block with name {}", newBlockType, blockName);
        UriTemplate URI_PATCHDATA = new UriTemplate(env.getProperty("uri.patchdata"));
        boolean result = restTemplate.patchForObject(URI_PATCHDATA.expand(blockName, newBlockType), blockName, Boolean.class);
        log.info("Block updated on server: "+ result);
        return result;
    }

}
