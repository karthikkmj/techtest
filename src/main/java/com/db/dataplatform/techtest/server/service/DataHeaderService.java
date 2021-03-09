package com.db.dataplatform.techtest.server.service;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;

public interface DataHeaderService {
    void saveHeader(DataHeaderEntity entity);
    DataHeaderEntity getHeaderByBlockName(String blockName);
    int updateBlockType(BlockTypeEnum blockType, String name);
}
