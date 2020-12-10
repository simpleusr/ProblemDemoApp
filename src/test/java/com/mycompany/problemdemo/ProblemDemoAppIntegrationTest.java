package com.mycompany.problemdemo;

import static org.junit.Assert.assertNotNull;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import reactor.blockhound.BlockHound;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = com.mycompany.problemdemo.ProblemDemoAppMain.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@DirtiesContext
public class ProblemDemoAppIntegrationTest {

    private static Logger logger = LoggerFactory.getLogger(ProblemDemoAppIntegrationTest.class);

    static {
        System.out.println(Thread.currentThread());
        new Exception().printStackTrace();
        BlockHound.install(new ProblemDemoAppBlockHoundIntegration());

    }

    @ClassRule
    public static WireMockRule externalServiceWireMockRule           = new WireMockRule(WireMockConfiguration.wireMockConfig().dynamicPort());

    private static String      externalServiceWireMockApiResourceUri = "test";

    @Autowired
    private WebTestClient      webTestClient;

    @DynamicPropertySource
    static void addDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("external.service.api.baseUrl", () -> "http://localhost:" + externalServiceWireMockRule.port());
        registry.add("external.service.api.resourceUri", () -> externalServiceWireMockApiResourceUri);
    }

    @Test
    public void toDoTestComplete() throws Exception {

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/" + externalServiceWireMockApiResourceUri))
                                 .willReturn(WireMock.aResponse()
                                                     .withStatus(200)
                                                     .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                                     .withBody("{\r\n" + "   \"unique_id\": \"dummyVal\"\r\n" + "}")));

        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder.path("/demo/")
                                                  .queryParam("param1", "param1Val")
                                                  .build())
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody()
                     .consumeWith(response -> {
                         logger.info(String.format("response : %s", response));
                         assertNotNull(response.getResponseBody());
                         return;
                     });

    }

}
