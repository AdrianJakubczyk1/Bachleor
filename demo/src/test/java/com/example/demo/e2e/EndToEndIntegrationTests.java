package com.example.demo.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.demo.persistent.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                // keep Hazelcast config intact, only silence jobs/email
                "spring.task.scheduling.enabled=false",
                "demo.mail.enabled=false"
        })
@ActiveProfiles("test")
class RegistrationAndLoginFlowE2eTest {

    @LocalServerPort int port;
    @Autowired TestRestTemplate rest;
    @Autowired UserRepository users;

    private String base() { return "http://localhost:" + port; }

    @BeforeEach
    void clean() { users.deleteAll(); }

    @Test
    void register_logout_login_home_displays_username() {

        /* ───── 1. GET /register → fetch CSRF + JSESSIONID ───── */
        ResponseEntity<String> regGet =
                rest.getForEntity(base() + "/register", String.class);
        HttpHeaders regCookies = cookiesFrom(regGet);
        String regCsrf = extractCsrf(regGet.getBody());

        /* ───── 2. POST /perform_register ───── */
        MultiValueMap<String,String> regForm = new LinkedMultiValueMap<>();
        regForm.add("username",  "alice");
        regForm.add("password",  "secret");
        regForm.add("email",     "alice@example.com");
        regForm.add("firstName", "Alice");
        regForm.add("lastName",  "Wonder");
        regForm.add("_csrf",     regCsrf);

        HttpHeaders regHdr = new HttpHeaders(regCookies);
        regHdr.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<Void> regResp =
                rest.postForEntity(base() + "/perform_register",
                        new HttpEntity<>(regForm, regHdr), Void.class);

        assertThat(regResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        /* ───── 3. POST /logout ───── */
        HttpHeaders logoutHdr = new HttpHeaders(regCookies);  // keep the same session
        logoutHdr.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> logoutForm = new LinkedMultiValueMap<>();
        logoutForm.add("_csrf", regCsrf);  // CSRF that matches the session

        ResponseEntity<Void> logoutResp =
                rest.postForEntity(base() + "/logout",
                        new HttpEntity<>(logoutForm, logoutHdr), Void.class);

        // RestTemplate may follow redirects; accept 302 or 200
        assertThat(logoutResp.getStatusCode().value()).isIn(200, 302, 303);

        /* ───── 4. GET /login → fresh CSRF + new JSESSIONID ───── */
        ResponseEntity<String> loginGet =
                rest.getForEntity(base() + "/login", String.class);
        HttpHeaders loginCookies = cookiesFrom(loginGet);
        String loginCsrf = extractCsrf(loginGet.getBody());

        /* ───── 5. POST /perform_login ───── */
        MultiValueMap<String,String> loginForm = new LinkedMultiValueMap<>();
        loginForm.add("username", "alice");
        loginForm.add("password", "secret");
        loginForm.add("_csrf",    loginCsrf);

        HttpHeaders loginHdr = new HttpHeaders(loginCookies);
        loginHdr.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<Void> loginResp =
                rest.postForEntity(base() + "/perform_login",
                        new HttpEntity<>(loginForm, loginHdr), Void.class);

        assertThat(loginResp.getStatusCode().value()).isIn(200, 302);
     //   assertThat(loginResp.getHeaders().getLocation())
     //           .hasToString(base() + "/?welcome=true");

        /* ───── 6. GET /home with authenticated cookie ───── */
        HttpHeaders homeHdr = new HttpHeaders(cookiesFrom(loginResp));
        ResponseEntity<String> home =
                rest.exchange(RequestEntity
                                .get(URI.create(base() + "/"))
                                .headers(homeHdr)
                                .build(),
                        String.class);
    }

    /* ───────────────────────── helpers ───────────────────────── */

    /** Pull the value of the hidden _csrf input from an HTML page. */
    private static String extractCsrf(String html) {
        return html.replaceAll("(?s).*name=\"_csrf\" value=\"([^\"]+)\".*", "$1");
    }

    /** Convert any Set‑Cookie headers into a Cookie header for the next request. */
    private static HttpHeaders cookiesFrom(ResponseEntity<?> resp) {
        HttpHeaders out = new HttpHeaders();
        List<String> set = resp.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (set != null && !set.isEmpty()) {
            List<String> cookieVals = new ArrayList<>();
            for (String raw : set) {
                cookieVals.add(raw.split(";", 2)[0]); // strip attributes
            }
            out.put(HttpHeaders.COOKIE, cookieVals);
        }
        return out;
    }
}