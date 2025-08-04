package br.com.orm.config;

import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrokerConfig implements ArtemisConfigurationCustomizer {
    
    @Override
    public void customize(org.apache.activemq.artemis.core.config.Configuration configuration) {
        try {
            configuration.addAcceptorConfiguration("tcp", "tcp://0.0.0.0:61616");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar Artemis", e);
        }
    }
}