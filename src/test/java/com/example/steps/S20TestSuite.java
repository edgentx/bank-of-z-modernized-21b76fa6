import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("features")
public class S20TestSuite {
    // JUnit 5 Suite configuration to run Cucumber features
}
