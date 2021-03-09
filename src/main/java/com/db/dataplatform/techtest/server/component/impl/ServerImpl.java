package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.service.DataHeaderService;
import com.db.dataplatform.techtest.server.service.HadoopUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

    private final DataBodyService dataBodyServiceImpl;
    private final DataHeaderService dataHeaderServiceImpl;
    private final HadoopUpdateService hadoopUpdateServiceImpl;
    private final ModelMapper modelMapper;

    @Autowired
    Environment env;

    /**
     * @param envelope
     * @return true if there is a match with the client provided checksum.
     */
    @Override
    public boolean saveDataEnvelope(DataEnvelope envelope) throws NoSuchAlgorithmException {

        // Validate Checksum
        if(!validateChecksum(envelope)) {
            log.info("Checksum failed for {} ignoring message ",envelope.getDataHeader().getName());
            return false;
        }

        // Save to persistence.
        persist(envelope);
        log.info("Data persisted successfully, data name: {}", envelope.getDataHeader().getName());

        return true;
    }

    @Override
    public List<DataEnvelope> findDataEnvelopesByBlockType(BlockTypeEnum blockType) {

        log.info("Find data with blocktype: {}", blockType);
        List<DataBodyEntity> envelopes = dataBodyServiceImpl.getDataByBlockType(blockType);
        List<DataEnvelope> dataEnvelopes = envelopes.stream().map(t -> mapper(t)).collect(Collectors.toList());

        return dataEnvelopes;
    }

    @Override
    @Transactional
    public boolean patchDataByBlockName(String blockName, String newBlockType) {
        DataHeaderEntity dataHeaderEntity = dataHeaderServiceImpl.getHeaderByBlockName(blockName);

        if(dataHeaderEntity == null) {
            log.info("Data Header for name {} not found", blockName);
            return false;
        }

        dataHeaderEntity.setBlocktype(BlockTypeEnum.valueOf(newBlockType));
        return updateData(dataHeaderEntity)==1;
    }

    private boolean validateChecksum(DataEnvelope envelope) throws NoSuchAlgorithmException {

        String dataBody = envelope.getDataBody().getDataBody();
        String checksum = envelope.getDataBody().getChecksum();

        String expectedDigest = getMD5Hash(dataBody);
        return expectedDigest.equals(checksum);
    }

    private void persist(DataEnvelope envelope) {
        log.info("Persisting data with attribute name: {}", envelope.getDataHeader().getName());
        DataHeaderEntity dataHeaderEntity = modelMapper.map(envelope.getDataHeader(), DataHeaderEntity.class);

        DataBodyEntity dataBodyEntity = modelMapper.map(envelope.getDataBody(), DataBodyEntity.class);
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);

        saveData(dataBodyEntity);
    }

    private void saveData(DataBodyEntity dataBodyEntity) {
        dataBodyServiceImpl.saveDataBody(dataBodyEntity);
    }

    private DataEnvelope mapper(DataBodyEntity t) {

        DataBody db = new DataBody(t.getDataBody());
        DataHeader dh = DataHeader.builder().name(t.getDataHeaderEntity().getName())
                .blockType(t.getDataHeaderEntity().getBlocktype()).build();

        return new DataEnvelope(dh, db);
    }

    private String getMD5Hash(String msg) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return DatatypeConverter.printHexBinary(md.digest(msg.getBytes())).toUpperCase();
    }

    private int updateData(DataHeaderEntity dataHeaderEntity) {
        return dataHeaderServiceImpl.updateBlockType(dataHeaderEntity.getBlocktype(), dataHeaderEntity.getName());
    }


    public CompletableFuture<HttpStatus> updateDataLake(String data) {
        return hadoopUpdateServiceImpl.updateDataLake(env.getProperty("hadoop.datalake.url"), data);
    }
}
