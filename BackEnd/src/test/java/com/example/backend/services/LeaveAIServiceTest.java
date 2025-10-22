package com.example.backend.services;

import com.example.backend.entities.Leave;
import com.example.backend.entities.LeaveType;
import com.example.backend.models.LeaveAnalysis;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class LeaveAIServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private LeaveAIService leaveAIService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        leaveAIService = new LeaveAIService(restTemplate, objectMapper);
    }

    @Test
    void analyzeLeaveRequest_Success() throws Exception {
        // Arrange
        Leave leave = new Leave();
        leave.setType(LeaveType.ANNUAL);
        leave.setStart_date(new Date());
        leave.setEnd_date(new Date());
        leave.setReason("Vacation with family");

        // Mock API response
        String mockResponse = "{\"confidenceScore\": 0.85, \"analysis\": \"Valid request\", \"recommended\": true}";
        JsonNode mockJsonNode = new ObjectMapper().readTree(mockResponse);

        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(
            ResponseEntity.ok(mockJsonNode)
        );
        when(objectMapper.readTree(anyString())).thenReturn(mockJsonNode);

        // Act
        LeaveAnalysis result = leaveAIService.analyzeLeaveRequest(leave);

        // Assert
        assertNotNull(result);
        assertEquals(0.85, result.getConfidenceScore());
        assertEquals("Valid request", result.getAnalysisResult());
        assertTrue(result.getRecommendedApproval());
    }

    @Test
    void analyzeLeaveRequest_Error() {
        // Arrange
        Leave leave = new Leave();
        when(restTemplate.postForEntity(anyString(), any(), any()))
            .thenThrow(new RuntimeException("API Error"));

        // Act
        LeaveAnalysis result = leaveAIService.analyzeLeaveRequest(leave);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result.getConfidenceScore());
        assertTrue(result.getAnalysisResult().contains("Error analyzing leave request"));
        assertFalse(result.getRecommendedApproval());
    }
}