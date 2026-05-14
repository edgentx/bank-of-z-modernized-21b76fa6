package com.example.deploy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.data.mongodb.uri=mongodb://unresolvable-vforce-mongo:27017/bank"
})
@ActiveProfiles("vforce_dev")
class VforceDevSmokeEndpointStartupTest {

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Test
  void smokeEndpointsStartEvenWhenDevMongoIsNotResolvable() {
    assertSmokeOk("/");
    assertSmokeOk("/api");
    assertSmokeOk("/api/health");
  }

  private void assertSmokeOk(String path) {
    ResponseEntity<String> response =
        restTemplate.getForEntity("http://localhost:" + port + path, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("{\"status\":\"UP\"}", response.getBody());
  }
}
