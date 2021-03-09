package com.db.dataplatform.techtest.server.component;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Server {
    boolean saveDataEnvelope(DataEnvelope envelope) throws IOException, NoSuchAlgorithmException;
    List<DataEnvelope> findDataEnvelopesByBlockType(BlockTypeEnum blockType);
    boolean patchDataByBlockName(String blockName, String newBlockType);
    CompletableFuture<HttpStatus> updateDataLake(String data);
}
