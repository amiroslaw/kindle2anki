package ovh.miroslaw.kindle2anki;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import ovh.miroslaw.kindle2anki.service.MWDictionaryProvider;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final KeyMWInterceptor keyMWInterceptor;

    @Bean
    public MWDictionaryProvider mwClient() {
        RestClient client = RestClient
                .builder()
                .baseUrl("https://dictionaryapi.com/api/v3/references/learners/json/")
                .requestInterceptor(keyMWInterceptor)
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(client))
                .build();
        return factory.createClient(MWDictionaryProvider.class);
    }

}
