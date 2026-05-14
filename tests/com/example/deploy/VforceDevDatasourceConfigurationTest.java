package com.example.deploy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.FileSystemResource;

class VforceDevDatasourceConfigurationTest {

  private static final String LOCALHOST_DB2 = "localhost:50000";
  private static final String H2_DB2_URL =
      "jdbc:h2:mem:bank-vforce-dev;MODE=DB2;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";

  @Test
  void vforceDevProfileDefaultsToEmbeddedDb2CompatibleHistoryStore() throws IOException {
    Path path = Path.of("src/main/resources/application-vforce_dev.properties");
    Properties props = loadProperties(path);

    assertEquals("${SPRING_DATASOURCE_URL:" + H2_DB2_URL + "}",
        props.getProperty("spring.datasource.url"));
    assertEquals("${SPRING_DATASOURCE_DRIVER_CLASS_NAME:org.h2.Driver}",
        props.getProperty("spring.datasource.driver-class-name"));
    assertEquals("${SPRING_JPA_DATABASE_PLATFORM:org.hibernate.dialect.H2Dialect}",
        props.getProperty("spring.jpa.database-platform"));
    assertFalse(props.getProperty("spring.datasource.url").contains(LOCALHOST_DB2),
        "vforce_dev must never fall back to DB2 on localhost:50000");
    assertFalse(Files.readString(path).contains(LOCALHOST_DB2),
        "vforce_dev profile must not reintroduce the old localhost DB2 literal");
  }

  @Test
  void microk8sOverlayActivatesVforceDevMockHistoryStore() {
    Properties props = loadYaml("deploy/helm/teller/values-microk8s.yaml");

    assertEquals("k8s,vforce_dev", props.getProperty("backend.config.SPRING_PROFILES_ACTIVE"));
    assertEquals(H2_DB2_URL, props.getProperty("backend.config.SPRING_DATASOURCE_URL"));
    assertEquals("sa", props.getProperty("backend.config.SPRING_DATASOURCE_USERNAME"));
    assertEquals("org.h2.Driver",
        props.getProperty("backend.config.SPRING_DATASOURCE_DRIVER_CLASS_NAME"));
    assertEquals("org.hibernate.dialect.H2Dialect",
        props.getProperty("backend.config.SPRING_JPA_DATABASE_PLATFORM"));
    assertEquals("", props.getProperty("backend.secrets.SPRING_DATASOURCE_PASSWORD"));
    assertFalse(props.getProperty("backend.config.SPRING_DATASOURCE_URL").contains(LOCALHOST_DB2),
        "rendered vforce_dev config must not point DB2 history at localhost");
  }

  @Test
  void containerImageDefaultsToVforceDevProfileWhenPlatformDoesNotSetOne() throws IOException {
    String dockerfile = Files.readString(Path.of("Dockerfile"));

    assertTrue(dockerfile.contains("SPRING_PROFILES_DEFAULT=\"vforce_dev\""),
        "standalone dev deploy containers must load the vforce_dev datasource defaults");
  }

  @Test
  void helmRolloutPullsFreshLatestImageAndChangesPodTemplateOnUpgrade() throws IOException {
    Properties props = loadYaml("deploy/helm/teller/values.yaml");
    String backendDeployment =
        Files.readString(Path.of("deploy/helm/teller/templates/backend-deployment.yaml"));

    assertEquals("Always", props.getProperty("global.imagePullPolicy"),
        "mutable dev image tags must be pulled on each fresh pod");
    assertTrue(backendDeployment.contains("bank.example.com/helm-release-revision"),
        "helm upgrades must change the pod template even when the image tag stays latest");
    assertTrue(backendDeployment.contains(".Release.Revision"),
        "the rollout annotation must track the actual Helm release revision");
  }

  private static Properties loadProperties(Path path) throws IOException {
    Properties props = new Properties();
    try (Reader reader = Files.newBufferedReader(path)) {
      props.load(reader);
    }
    return props;
  }

  private static Properties loadYaml(String path) {
    YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
    yaml.setResources(new FileSystemResource(path));
    Properties props = yaml.getObject();
    assertNotNull(props);
    return props;
  }
}
