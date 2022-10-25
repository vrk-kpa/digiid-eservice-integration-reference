package fi.dvv.digiid.ho.op.restservice.config;

import fi.dvv.digiid.ho.common.spring.DigiIdConfiguration;
import fi.dvv.digiid.ho.op.restservice.service.oidc.configuration.OIDCConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.IdGenerator;

import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@Configuration
@ConfigurationProperties(prefix = DigiIdConfiguration.CONFIG_PREFIX_OP)
public class RestServiceConfiguration {
    private String siopRedirect;

    private List<OIDCConfiguration.OidcClient> oidcClients;

    private String oidcUrl;

    private boolean useTestAcrValues;

    @Data
    public static class RedisProperties {
        private String host;
        private int port;
        private String authtoken;
    }

    private RedisProperties redis;

    @Bean
    public RedisConnectionFactory redisClusterConnectionFactory() {
        if (redis.getAuthtoken() == null || redis.getAuthtoken().isEmpty()) {
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setHostName(redis.getHost());
            redisStandaloneConfiguration.setPort(redis.getPort());
            return new LettuceConnectionFactory(redisStandaloneConfiguration);
        }

        List<String> nodes = Collections.singletonList(redis.getHost());
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(nodes);
        clusterConfiguration.setPassword(RedisPassword.of(redis.getAuthtoken()));

        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .useSsl()
                .disablePeerVerification()
                .build();
        return new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
    }

    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public IdGenerator idGenerator() {
        return UUID::randomUUID;
    }

    @Bean
    public OIDCConfiguration oidcConfiguration() {
        return new OIDCConfiguration(oidcClients);
    }
}
