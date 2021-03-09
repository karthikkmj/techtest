package com.db.dataplatform.techtest.server.service.impl;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataHeaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DataHeaderServiceImpl implements com.db.dataplatform.techtest.server.service.DataHeaderService {

    private final DataHeaderRepository dataHeaderRepository;

    @Override
    public void saveHeader(DataHeaderEntity entity) {
        dataHeaderRepository.save(entity);
    }

    @Override
    @Transactional
    public int updateBlockType(BlockTypeEnum blockType, String name) {
        return dataHeaderRepository.updateBlocktype(blockType.name(),name);
    }

    @Override
    public DataHeaderEntity getHeaderByBlockName(String blockName) {
        return dataHeaderRepository.findByName(blockName).orElse(null);
    }
}
