package org.alexp.pactbug;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "provider", port = "9000", providerType = ProviderType.SYNCH)
class PactBugApplicationTests {

//    private final TestInfo testInfo;
//
//    PactBugApplicationTests(TestInfo testInfo) {
//
//        this.testInfo = testInfo;
//    }

    @Test
    @PactTestFor(providerName = "provider", pactMethod = "getPact")
    void testPact() {

        final RestTemplate restTemplate = new RestTemplate();

        final String forObject = restTemplate.getForObject("http://localhost:9000/test", String.class);

        Assertions.assertNotNull(forObject);
    }

    @Pact(provider = "provider", consumer = "consumer")
    RequestResponsePact getPact(PactDslWithProvider builder) {

        final DslPart message = new PactDslJsonBody().object("test").stringType("message").closeObject();

        final Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);

        return builder.given("a state")
                      .uponReceiving("a message")
                      .method("GET")
                      .path("/test")
                      .willRespondWith()
                      .headers(headers)
                      .status(200)
                      .body(message)
                      .toPact();
    }
}
