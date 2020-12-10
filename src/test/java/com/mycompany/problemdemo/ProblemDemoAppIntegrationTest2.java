package com.mycompany.problemdemo;

import static org.junit.Assert.assertNotNull;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
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
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

import reactor.blockhound.BlockHound;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = com.mycompany.problemdemo.ProblemDemoAppMain.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@DirtiesContext
public class ProblemDemoAppIntegrationTest2 {

    private static Logger logger = LoggerFactory.getLogger(ProblemDemoAppIntegrationTest2.class);

    static {
        System.out.println(Thread.currentThread());
        new Exception().printStackTrace();
        BlockHound.install(new ProblemDemoAppBlockHoundIntegration());

    }

    @ClassRule
    public static MockServerContainer mockServerContainer                     = new MockServerContainer(DockerImageName.parse("jamesdbloom/mockserver:mockserver-5.5.4"));

    private static String             externalServiceMockServerApiResourceUri = "test";

    @Autowired
    private WebTestClient             webTestClient;

    @DynamicPropertySource
    static void addDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("external.service.api.baseUrl", () -> "http://" + mockServerContainer.getHost() + ":" + mockServerContainer.getServerPort());
        registry.add("external.service.api.resourceUri", () -> externalServiceMockServerApiResourceUri);
    }

    @Test
    public void toDoTestComplete() throws Exception {

        MockServerClient mockServerClient = new MockServerClient(mockServerContainer.getHost(), mockServerContainer.getServerPort());
        try {

            mockServerClient.when(HttpRequest.request().withMethod("POST").withPath("/" + externalServiceMockServerApiResourceUri))
                            .respond(HttpResponse.response()
                                                 .withStatusCode(200)
                                                 .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                                 .withBody("{\r\n" + "   \"unique_id\": \"dummyVal\"\r\n" + "}"));

            webTestClient.get()
                         .uri(uriBuilder -> uriBuilder.path("/demo/").queryParam("param1", "param1Val").build())
                         .exchange()
                         .expectStatus()
                         .isOk()
                         .expectBody()
                         .consumeWith(response -> {
                             logger.info(String.format("response : %s", response));
                             assertNotNull(response.getResponseBody());
                             return;
                         });

        } finally {
            mockServerClient.close();
        }

    }

}
