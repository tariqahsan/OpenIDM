package mil.dtic.openidm.service;

import mil.dtic.openidm.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class OpenIDMService {

    private final WebClient webClient;
    private final Logger log = LoggerFactory.getLogger(OpenIDMService.class);

    public OpenIDMService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Get user from OpenIDM by id. The Authorization bearer token is forwarded from the client.
     * This response is cached by the provided cache manager under 'users' with key=userId.
     */
    // @Cacheable(value = "users", key = "#userId")
    // public UserDto getUser(String userId, String bearerToken) {
    //     try {
    //         log.info("Calling OpenIDM GET for user: {}", userId);
    //         UserDto user = webClient.get()
    //                 .uri(uriBuilder -> uriBuilder.path("/managed/user/{id}").build(userId))
    //                 .header("Authorization", bearerToken) // pass raw header value, e.g. "Bearer <token>"
    //                 .retrieve()
    //                 .onStatus(HttpStatus::is4xxClientError, resp -> {
    //                     log.warn("OpenIDM returned 4xx for user {}: {}", userId, resp.statusCode());
    //                     return resp.createException();
    //                 })
    //                 .onStatus(HttpStatus::is5xxServerError, resp -> {
    //                     log.error("OpenIDM returned 5xx for user {}: {}", userId, resp.statusCode());
    //                     return resp.createException();
    //                 })
    //                 .bodyToMono(UserDto.class)
    //                 .doOnNext(u -> log.info("Received user from OpenIDM: {}", u))
    //                 .block();

    //         // log again before returning (also useful for debugging)
    //         log.info("Returning user object to controller: {}", user);
    //         return user;
    //     } catch (WebClientResponseException e) {
    //         log.error("OpenIDM client error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
    //         throw e;
    //     } catch (Exception e) {
    //         log.error("Failed to call OpenIDM GET for user {}: {}", userId, e.getMessage(), e);
    //         throw new RuntimeException(e);
    //     }
    // }

    @Cacheable(value = "users", key = "#userId")
    //public UserDto getUser(String userId, String bearerToken) {
    public UserDto getUser(String userId) {
        log.info("Cache miss for userId: {}, calling OpenIDM REST API...", userId);

        try {
            return webClient.get()
                    .uri("/managed/user/{id}", userId)
                    //.header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                    .header("X-OpenIDM-Username", "openidm-admin")
                    .header("X-OpenIDM-Password", "openidm-admin")
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError(),
                            resp -> {
                                log.warn("OpenIDM returned 4xx for user {}: {}", userId, resp.statusCode());
                                return resp.createException();
                            })
                    .onStatus(
                            status -> status.is5xxServerError(),
                            resp -> {
                                log.error("OpenIDM returned 5xx for user {}: {}", userId, resp.statusCode());
                                return resp.createException();
                            })
                    .bodyToMono(UserDto.class)
                    .doOnNext(u -> log.info("Received user from OpenIDM: {}", u))
                    .block();

        } catch (WebClientResponseException e) {
            log.error("WebClient error calling OpenIDM (status={}): {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw e; // rethrow to propagate error
        } catch (WebClientRequestException e) {
            log.error("Request failed when calling OpenIDM for user {}: {}", userId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calling OpenIDM for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("OpenIDM call failed", e);
        }
    }


    /**
     * Create a user in OpenIDM. Accepts a raw JSON payload (from client) and forwards it.
     * Returns the server response mapped to String for simplicity.
     */
    public String createUser(String userJson, String bearerToken) {
        try {
            log.info("Calling OpenIDM POST to create user. Payload: {}", userJson);
            String response = webClient.post()
                    .uri("/managed/user")
                    //.header("Authorization", bearerToken)
                    .header("X-OpenIDM-Username", "openidm-admin")
                    .header("X-OpenIDM-Password", "openidm-admin")
                    .bodyValue(userJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(resp -> log.info("OpenIDM POST response: {}", resp))
                    .block();

            log.info("Create user response returned to controller");
            return response;
        } catch (WebClientResponseException e) {
            log.error("OpenIDM client error on POST: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to call OpenIDM POST: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
