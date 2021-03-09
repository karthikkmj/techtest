package com.db.dataplatform.techtest.server.api.controller;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Controller
@RequestMapping("/dataserver")
@RequiredArgsConstructor
@Validated
public class ServerController {

    private final Server server;

    @PostMapping(value = "/pushdata", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> pushData(@Valid @RequestBody DataEnvelope dataEnvelope) throws IOException, NoSuchAlgorithmException {

        log.info("Data envelope received: {}", dataEnvelope.getDataHeader().getName());
        boolean checksumPass = server.saveDataEnvelope(dataEnvelope);

        // Update Hadoop - Async
        // Failure or delay on the Hadoop update does not affect the current post request
        if(checksumPass) {
            CompletableFuture<Void> future = server.updateDataLake(dataEnvelope.toString()).thenAccept(status -> log.info("Updated data lake for: " +dataEnvelope.getDataHeader().getName() ));
            future.exceptionally(e -> {
                log.error("Failed to update data lake for data: {}",dataEnvelope.getDataHeader().getName());
                return null;
            });
        }

        return ResponseEntity.ok(checksumPass);

    }

    @GetMapping(value = "/data/{blockType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DataEnvelope>> getDataEnvelopesByBlockType(@PathVariable("blockType") String blockType) throws IOException, NoSuchAlgorithmException {

        log.info("Request for BlockType received: {}", blockType);
        return ResponseEntity.ok(server.findDataEnvelopesByBlockType(BlockTypeEnum.valueOf(blockType)));
    }

    @PatchMapping(value = "/update/{name}/{newBlockType}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> patchDataByBlockName(@PathVariable("name") String name, @PathVariable("newBlockType") String newBlockType) {

        log.info("Patch Request for BlockName {} received to update to type {}", name, newBlockType);
        return ResponseEntity.ok(server.patchDataByBlockName(name, newBlockType));
    }

}
