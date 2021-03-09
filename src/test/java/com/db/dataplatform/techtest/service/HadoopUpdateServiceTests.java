package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataHeaderRepository;
import com.db.dataplatform.techtest.server.service.DataHeaderService;
import com.db.dataplatform.techtest.server.service.HadoopUpdateService;
import com.db.dataplatform.techtest.server.service.impl.DataHeaderServiceImpl;
import com.db.dataplatform.techtest.server.service.impl.HadoopUpdateServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataHeaderEntity;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HadoopUpdateServiceTests {

    @Mock
    private RestTemplate restTemplateMock;
    private HadoopUpdateService hadoopUpdateService;
    private static final String url = "http://localhost:8090/hadoopserver/pushbigdata";
    private static final String DUMMY_DATA = "Dummy_Data";

    @Before
    public void setup() {
        hadoopUpdateService = new HadoopUpdateServiceImpl(restTemplateMock);

    }

    @Test
    public void shouldUpdateDataLakeAsExpected(){
        hadoopUpdateService.updateDataLake(url, DUMMY_DATA);

        verify(restTemplateMock, times(1))
                .postForObject(eq(url), eq(DUMMY_DATA), eq(HttpStatus.class));
    }

}
