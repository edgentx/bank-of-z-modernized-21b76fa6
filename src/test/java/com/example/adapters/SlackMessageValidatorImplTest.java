package com.example.adapters;

import com.example.domain.vforce360.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class SlackMessageValidatorImplTest {

    private final SlackMessageValidatorImpl validator = new SlackMessageValidatorImpl();

    @Test
    void validate_WhenBodyContainsUrl_ReturnsTrue() {
        ReportDefectCmd cmd = new ReportDefectCmd("1", "Bug", "Desc", "http://github.com/repo/issue/1", Map.of());
        String body = "Issue reported. Link: http://github.com/repo/issue/1";
        
        assertTrue(validator.validate(cmd, body));
    }

    @Test
    void validate_WhenBodyDoesNotContainUrl_ReturnsFalse() {
        ReportDefectCmd cmd = new ReportDefectCmd("1", "Bug", "Desc", "http://github.com/repo/issue/1", Map.of());
        String body = "Issue reported. Link missing.";
        
        assertFalse(validator.validate(cmd, body));
    }

    @Test
    void validate_WhenUrlIsNull_ReturnsFalse() {
        ReportDefectCmd cmd = new ReportDefectCmd("1", "Bug", "Desc", null, Map.of());
        String body = "Issue reported.";
        
        assertFalse(validator.validate(cmd, body));
    }
}
