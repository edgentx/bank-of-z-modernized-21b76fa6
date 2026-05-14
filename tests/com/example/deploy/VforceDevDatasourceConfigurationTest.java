package com.example.deploy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mongodb.ConnectionString;
import com.mongodb.MongoCredential;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.FileSystemResource;

class VforceDevDatasourceConfigurationTest {

  private static final String LOCALHOST_DB2 = "localhost:50000";
  private static final String LOCALHOST_MONGO = "localhost:27017";
  private static final String VFORCE_DEV_MONGO_URI =
      "mongodb://bank:bank-mongo-dev-pw@bank-mongo:27017/bank?authSource=admin";
  private static final String APP_PORT = "8000";
  private static final String FRONTEND_SERVICE_PORT = "80";
  private static final String FRONTEND_CONTAINER_PORT = "8080";
  private static final String H2_DB2_URL =
      "jdbc:h2:mem:bank-vforce-dev;MODE=DB2;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";

  @Test
  void baseProfileDefaultsToEmbeddedHistoryStoreWhenPlatformSetsNoDatasource() throws IOException {
    Path path = Path.of("src/main/resources/application.properties");
    Properties props = loadProperties(path);

    assertEquals("${SPRING_DATASOURCE_URL:" + H2_DB2_URL + "}",
        props.getProperty("spring.datasource.url"));
    assertEquals("${SPRING_DATASOURCE_DRIVER_CLASS_NAME:org.h2.Driver}",
        props.getProperty("spring.datasource.driver-class-name"));
    assertEquals("${SPRING_JPA_DATABASE_PLATFORM:org.hibernate.dialect.H2Dialect}",
        props.getProperty("spring.jpa.database-platform"));
    assertFalse(Files.readString(path).contains(LOCALHOST_DB2),
        "base container defaults must not attempt DB2 against localhost:50000");
  }

  @Test
  void vforceDevProfileDefaultsToEmbeddedDb2CompatibleHistoryStore() throws IOException {
    Path path = Path.of("src/main/resources/application-vforce_dev.properties");
    Properties props = loadProperties(path);

    assertEquals("${SPRING_DATA_MONGODB_URI:" + VFORCE_DEV_MONGO_URI + "}",
        props.getProperty("spring.data.mongodb.uri"));
    assertEquals("${SPRING_DATA_MONGODB_AUTO_INDEX_CREATION:false}",
        props.getProperty("spring.data.mongodb.auto-index-creation"));
    assertAuthenticatedVforceDevMongoUri(VFORCE_DEV_MONGO_URI);
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
    assertFalse(props.containsKey("backend.config.SPRING_DATA_MONGODB_URI"),
        "vforce_dev Mongo credentials must render through Secret, not ConfigMap");
    assertEquals(VFORCE_DEV_MONGO_URI,
        props.getProperty("backend.secrets.SPRING_DATA_MONGODB_URI"));
    assertEquals("false",
        props.getProperty("backend.config.SPRING_DATA_MONGODB_AUTO_INDEX_CREATION"),
        "vforce_dev must not block smoke endpoints on Mongo index creation");
    assertAuthenticatedVforceDevMongoUri(props.getProperty("backend.secrets.SPRING_DATA_MONGODB_URI"));
    assertEquals(H2_DB2_URL, props.getProperty("backend.config.SPRING_DATASOURCE_URL"));
    assertEquals("sa", props.getProperty("backend.config.SPRING_DATASOURCE_USERNAME"));
    assertEquals("org.h2.Driver",
        props.getProperty("backend.config.SPRING_DATASOURCE_DRIVER_CLASS_NAME"));
    assertEquals("org.hibernate.dialect.H2Dialect",
        props.getProperty("backend.config.SPRING_JPA_DATABASE_PLATFORM"));
    assertEquals("", props.getProperty("backend.secrets.SPRING_DATASOURCE_PASSWORD"));
    assertEquals("false", props.getProperty("envoy.enabled"));
    assertEquals("false", props.getProperty("opa.enabled"));
    assertEquals("nginx", props.getProperty("kong.ingressClassName"),
        "vforce_dev API ingress must bind to the MicroK8s NGINX controller");
    assertFalse(props.getProperty("backend.config.SPRING_DATASOURCE_URL").contains(LOCALHOST_DB2),
        "rendered vforce_dev config must not point DB2 history at localhost");
  }

  @Test
  void containerImageDefaultsToVforceDevProfileWhenPlatformDoesNotSetOne() throws IOException {
    String dockerfile = Files.readString(Path.of("Dockerfile"));
    String nativeDockerfile = Files.readString(Path.of("Dockerfile.native"));

    assertTrue(dockerfile.contains("SPRING_PROFILES_DEFAULT=\"vforce_dev\""),
        "standalone dev deploy containers must load the vforce_dev datasource defaults");
    assertTrue(dockerfile.contains("SPRING_DATA_MONGODB_URI=\"" + VFORCE_DEV_MONGO_URI + "\""),
        "standalone dev deploy containers must default Mongo to the authenticated in-cluster service");
    assertFalse(dockerfile.contains("SPRING_DATA_MONGODB_URI=\"mongodb://localhost:27017/bank\""),
        "standalone dev deploy containers must not default Mongo to localhost");
    assertTrue(nativeDockerfile.contains("SPRING_PROFILES_DEFAULT=\"vforce_dev\""),
        "native standalone dev deploy containers must load the vforce_dev datasource defaults");
    assertTrue(nativeDockerfile.contains("SPRING_DATA_MONGODB_URI=\"" + VFORCE_DEV_MONGO_URI + "\""),
        "native standalone dev deploy containers must default Mongo to the authenticated in-cluster service");
    assertFalse(nativeDockerfile.contains("SPRING_DATA_MONGODB_URI=\"mongodb://localhost:27017/bank\""),
        "native standalone dev deploy containers must not default Mongo to localhost");
  }

  @Test
  void generatedDevDeploymentPortDefaultsStayAligned() throws IOException {
    Properties appProps = loadProperties(Path.of("src/main/resources/application.properties"));
    Properties helmProps = loadYaml("deploy/helm/teller/values.yaml");
    String dockerfile = Files.readString(Path.of("Dockerfile"));
    String nativeDockerfile = Files.readString(Path.of("Dockerfile.native"));

    assertEquals("${SERVER_PORT:" + APP_PORT + "}", appProps.getProperty("server.port"));
    assertEquals("${MANAGEMENT_SERVER_PORT:${SERVER_PORT:" + APP_PORT + "}}",
        appProps.getProperty("management.server.port"));
    assertEquals(APP_PORT, helmProps.getProperty("backend.service.port"));
    assertEquals(APP_PORT, helmProps.getProperty("backend.config.SERVER_PORT"));
    assertEquals(APP_PORT, helmProps.getProperty("backend.config.MANAGEMENT_SERVER_PORT"));
    assertTrue(dockerfile.contains("EXPOSE " + APP_PORT));
    assertTrue(dockerfile.contains("localhost:" + APP_PORT + "/actuator/health"));
    assertTrue(nativeDockerfile.contains("EXPOSE " + APP_PORT));
  }

  @Test
  void frontendServiceTargetsNginxRuntimePort() throws IOException {
    Properties helmProps = loadYaml("deploy/helm/teller/values.yaml");
    String deployment =
        Files.readString(Path.of("deploy/helm/teller/templates/frontend-deployment.yaml"));
    String service = Files.readString(Path.of("deploy/helm/teller/templates/frontend-service.yaml"));
    String dockerfile = Files.readString(Path.of("frontend/Dockerfile"));
    String nginxConf = Files.readString(Path.of("frontend/docker/nginx.conf"));

    assertEquals(FRONTEND_SERVICE_PORT, helmProps.getProperty("frontend.service.port"));
    assertEquals(FRONTEND_CONTAINER_PORT, helmProps.getProperty("frontend.containerPort"));
    assertTrue(deployment.contains("containerPort: {{ .Values.frontend.containerPort }}"),
        "frontend Deployment must publish the real nginx listener as the named http port");
    assertTrue(service.contains("targetPort: http"),
        "frontend Service should continue routing to the named container port");
    assertTrue(dockerfile.contains("EXPOSE " + FRONTEND_CONTAINER_PORT));
    assertTrue(nginxConf.contains("listen " + FRONTEND_CONTAINER_PORT + " default_server;"));
  }

  @Test
  void apiIngressRoutesBaseAndApiPrefixesForDeploymentSmokeChecks() {
    Properties props = loadYaml("deploy/helm/teller/values.yaml");

    assertEquals("/api", props.getProperty("kong.paths[0].path"));
    assertEquals("Prefix", props.getProperty("kong.paths[0].pathType"));
    assertEquals("/", props.getProperty("kong.paths[1].path"));
    assertEquals("Prefix", props.getProperty("kong.paths[1].pathType"));
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

  private static void assertAuthenticatedVforceDevMongoUri(String uri) {
    assertFalse(uri.contains(LOCALHOST_MONGO),
        "vforce_dev must never fall back to Mongo on localhost:27017");

    ConnectionString connectionString = new ConnectionString(uri);
    assertEquals(List.of("bank-mongo:27017"), connectionString.getHosts());
    assertEquals("bank", connectionString.getDatabase());

    MongoCredential credential = connectionString.getCredential();
    assertNotNull(credential);
    assertEquals("bank", credential.getUserName());
    assertEquals("bank-mongo-dev-pw", new String(credential.getPassword()));
    assertEquals("admin", credential.getSource());
  }

  private static Properties loadYaml(String path) {
    YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
    yaml.setResources(new FileSystemResource(path));
    Properties props = yaml.getObject();
    assertNotNull(props);
    return props;
  }
}
