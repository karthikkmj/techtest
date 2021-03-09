package com.db.dataplatform.techtest.server.persistence.repository;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DataHeaderRepository extends JpaRepository<DataHeaderEntity, Long> {

    Optional<DataHeaderEntity> findByName(String name);

    @Modifying(flushAutomatically = true)
    @Query(value = "UPDATE DATA_HEADER d SET d.BLOCKTYPE = :blocktype WHERE d.NAME = :name", nativeQuery = true)
    int updateBlocktype(@Param("blocktype") String blocktype, @Param("name") String name);

}
