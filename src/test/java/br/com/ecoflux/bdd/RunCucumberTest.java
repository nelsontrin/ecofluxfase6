package br.com.ecoflux.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "br.com.ecoflux.bdd",
        plugin = {"pretty", "html:target/cucumber-report.html", "json:target/cucumber-report.json", "junit:target/cucumber-junit.xml"}
)
public class RunCucumberTest {
}
