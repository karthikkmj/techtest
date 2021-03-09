package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.component.impl.ServerImpl;
import com.db.dataplatform.techtest.server.mapper.ServerMapperConfiguration;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.service.DataHeaderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static com.db.dataplatform.techtest.TestDataHelper.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ServerServiceTests {

    @Mock
    private DataBodyService dataBodyServiceImplMock;
    @Mock
    private DataHeaderService dataHeaderServiceImplMock;
    private ModelMapper modelMapper;
    private DataBodyEntity expectedDataBodyEntity;
    private DataEnvelope testDataEnvelope;
    private DataEnvelope testDataEnvelopeWrongChecksum;
    private List<DataBodyEntity> testDataBodyEntities;
    private Server server;

    @Before
    public void setup() {
        ServerMapperConfiguration serverMapperConfiguration = new ServerMapperConfiguration();
        modelMapper = serverMapperConfiguration.createModelMapperBean();

        testDataEnvelope = createTestDataEnvelopeApiObject();
        testDataEnvelopeWrongChecksum = createTestDataEnvelopeApiObjectWithWrongChecksum();
        testDataBodyEntities = createTestDataBodyEntities();
        expectedDataBodyEntity = modelMapper.map(testDataEnvelope.getDataBody(), DataBodyEntity.class);
        expectedDataBodyEntity.setDataHeaderEntity(modelMapper.map(testDataEnvelope.getDataHeader(), DataHeaderEntity.class));

        server = new ServerImpl(dataBodyServiceImplMock, dataHeaderServiceImplMock, null, modelMapper);

        when(dataBodyServiceImplMock.getDataByBlockType(any(BlockTypeEnum.class))).thenReturn(testDataBodyEntities);
        when(dataHeaderServiceImplMock.getHeaderByBlockName(anyString())).thenReturn(expectedDataBodyEntity.getDataHeaderEntity());
        when(dataHeaderServiceImplMock.getHeaderByBlockName(eq(expectedDataBodyEntity.getDataHeaderEntity().getName())))
                .thenReturn(expectedDataBodyEntity.getDataHeaderEntity());
        when(dataHeaderServiceImplMock.updateBlockType(eq(BlockTypeEnum.BLOCKTYPEB),eq(expectedDataBodyEntity.getDataHeaderEntity().getName())))
                .thenReturn(1);

    }

    @Test
    public void shouldSaveDataEnvelopeAsExpected() throws NoSuchAlgorithmException, IOException {
        boolean success = server.saveDataEnvelope(testDataEnvelope);

        assertThat(success).isTrue();
        //verify(dataBodyServiceImplMock, times(1)).saveDataBody(eq(expectedDataBodyEntity));
    }

    @Test
    public void shouldRejectWrongChecksumDataEnvelopeAsExpected() throws NoSuchAlgorithmException, IOException {
        boolean result = server.saveDataEnvelope(testDataEnvelopeWrongChecksum);

        assertThat(result).isFalse();
        //verify(dataBodyServiceImplMock, times(1)).saveDataBody(eq(expectedDataBodyEntity));
    }

    @Test
    public void shouldReturnDataEnvelopesByBlockTypeAsExpected() {
        List<DataEnvelope> envelopes = server.findDataEnvelopesByBlockType(BlockTypeEnum.BLOCKTYPEA);
        assertThat(envelopes.size()==2);
    }

    @Test
    public void shouldPatchDataByBlockNameAsExpected() {
        boolean result = server.patchDataByBlockName(testDataEnvelope.getDataHeader().getName(), BlockTypeEnum.BLOCKTYPEB.name());
        assertThat(result).isTrue();
    }

    @Test
    public void shouldFailPatchDataByWrongBlockNameAsExpected() {
        boolean result = server.patchDataByBlockName("WRONG_NAME", BlockTypeEnum.BLOCKTYPEA.name());
        assertThat(result).isFalse();
    }

}
