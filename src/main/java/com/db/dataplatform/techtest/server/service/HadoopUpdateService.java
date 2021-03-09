package com.db.dataplatform.techtest.server.service;

import org.springframework.http.HttpStatus;

import java.util.concurrent.CompletableFuture;


public interface HadoopUpdateService {
    CompletableFuture<HttpStatus> updateDataLake(String url, String data) ;
}
