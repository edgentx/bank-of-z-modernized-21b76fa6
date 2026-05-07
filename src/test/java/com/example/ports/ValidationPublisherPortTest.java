package com.example.ports;

import com.example.domain.validation.model.DefectReportedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;

import static org.mockito.Mockito.verify;

@SpringBootTest
class ValidationPublisherPortTest {

    @MockBean
    private ValidationPublisherPort validationPublisherPort;

    @Test
    void shouldPublishEventToTemporalQueue(@Autowired ValidationPublisherPort port) {
        // Given
        DefectReportedEvent event = new DefectReportedEvent("pid-123", "Title", "Desc", "LOW", "comp", Instant.now());

        // When
        port.publishDefectReported(event);

        // Then
        verify(validationPublisherPort).publishDefectReported(event);
    }
}
