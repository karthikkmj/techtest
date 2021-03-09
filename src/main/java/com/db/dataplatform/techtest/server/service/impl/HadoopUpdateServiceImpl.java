package com.db.dataplatform.techtest.server.service.impl;

import com.db.dataplatform.techtest.server.service.HadoopUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class HadoopUpdateServiceImpl implements HadoopUpdateService {

    private final RestTemplate restTemplate;

    @Override
    @Async
    public CompletableFuture<HttpStatus> updateDataLake(String url, String data)  {
        HttpStatus results = restTemplate.postForObject(url, data, HttpStatus.class);
        return CompletableFuture.completedFuture(results);
    }

}
