package com.trc.test;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;

/**
 * Ident info test.
 *
 * @author gudaoxuri
 */
@ExtendWith({ SpringExtension.class })
@SpringBootTest(classes = BootTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("ident")
public class IdentInfoTest {

        @Autowired
        private TestRestTemplate testRestTemplate;

        @Test
        public void testIdentInfo() throws Exception {
                var resultWhite = testRestTemplate.getForEntity("/ident/white", Resp.class);
                Assertions.assertEquals(200, resultWhite.getStatusCode().value());
                Assertions.assertNotEquals(
                                "[]The request is missing [" + Dew.dewConfig.getSecurity().getIdentInfoFlag()
                                                + "] in header",
                                resultWhite.getBody().getMessage());

                var response = testRestTemplate.getForEntity("/ident-info", Resp.class);
                Assertions.assertEquals(200, response.getStatusCode().value());
                Assertions.assertTrue(response.getBody().getCode().contains("400"));
                Assertions.assertEquals(
                                "[]The request is missing [" + Dew.dewConfig.getSecurity().getIdentInfoFlag()
                                                + "] in header",
                                response.getBody().getMessage());

                HttpHeaders headers = new HttpHeaders();
                headers.set(Dew.dewConfig.getSecurity().getIdentInfoFlag(),
                                $.security.encodeStringToBase64("{\"token\":\"t1\",roles:[\"r1\"]}",
                                                StandardCharsets.UTF_8));
                var result = testRestTemplate.exchange("/test/ident-info",
                                HttpMethod.GET, new HttpEntity<>("", headers), Resp.class).getBody().getBody();
                var resultJson = $.json.toJson(result);
                Assertions.assertEquals("t1", resultJson.get("token").asText());
                Assertions.assertEquals("r1", resultJson.get("roles").get(0).asText());

        }

}
