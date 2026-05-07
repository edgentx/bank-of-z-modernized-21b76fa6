package com.vforce360;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * E2E Regression Test.
 * Ensures that the MAR Review section renders formatted HTML and NOT raw JSON.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class MarReportControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDB = new MongoDBContainer("mongo:6");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDB::getReplicaSetUrl);
    }

    @Autowired private MockMvc mvc;
    @Autowired private MongoClient mongoClient;
    @Autowired private ObjectMapper mapper;

    @BeforeEach
    void setupData() {
        MongoDatabase db = mongoClient.getDatabase("vforce360");
        MongoCollection<Document> col = db.getCollection("modernization_reports");
        col.insertOne(new Document()
            .append("projectId", "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1")
            .append("title", "Modernization Assessment")
            // Simulating the stored data which is markdown
            .append("rawContent", "# Assessment Summary\n\n* Critical systems identified.\n* Migration path: **Strangler Fig**.")
        );
    }

    @Test
    @DisplayName("S-1: MAR should render as HTML, not raw JSON")
    void marRendersAsHtmlNotRawJson() throws Exception {
        mvc.perform(get("/projects/21b76fa6-afb6-4593-9e1b-b5d7548ac4d1/mar/review")
                .accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            // 1. Content Type should be HTML, not JSON
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            // 2. Response must NOT contain raw JSON markers like curly braces from the DB
            .andExpect(content().string(not(containsString("{"))))
            .andExpect(content().string(not(containsString("}"))))
            // 3. Response must contain the RENDERED HTML content (h1, ul, li, strong)
            .andExpect(content().string(containsString("<h1>")))
            .andExpect(content().string(containsString("Assessment Summary")))
            .andExpect(content().string(containsString("<ul>")))
            .andExpect(content().string(containsString("<strong>Strangler Fig</strong>")));
    }

    @Test
    @DisplayName("S-1: Regression check - 404 if no MAR generated")
    void returns404IfMarNotFound() throws Exception {
        mvc.perform(get("/projects/non-existent/mar/review"))
            .andExpect(status().isNotFound());
    }
}