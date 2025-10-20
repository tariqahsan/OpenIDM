package mil.dtic.openidm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${openidm.base-url}")
    private String openidmBaseUrl;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        // increase memory limit for large payloads if needed
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();

        return builder
                .baseUrl(openidmBaseUrl)
                .exchangeStrategies(strategies)
                .build();
    }
}

