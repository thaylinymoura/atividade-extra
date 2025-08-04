package br.com.ufrn.imd.integrador.config;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.jms.ConnectionFactory;

@Configuration
public class JmsConfig {

    @Value("${spring.artemis.broker-url}")
    private String brokerUrl;

    @Value("${spring.artemis.user}")
    private String user;

    @Value("${spring.artemis.password}")
    private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        try {
            connectionFactory.setBrokerURL(brokerUrl);
            connectionFactory.setUser(user);
            connectionFactory.setPassword(password);
            
            // Configurar reconexão automática
            connectionFactory.setRetryInterval(1000);
            connectionFactory.setRetryIntervalMultiplier(2.0);
            connectionFactory.setMaxRetryInterval(30000);
            connectionFactory.setReconnectAttempts(-1); // -1 = tentar reconectar indefinidamente
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar ConnectionFactory: " + e.getMessage(), e);
        }
        
        return connectionFactory;
    }

    @Bean
    public JmsComponent jms(ConnectionFactory connectionFactory) {
        JmsComponent jmsComponent = JmsComponent.jmsComponent(connectionFactory);
        jmsComponent.setConnectionFactory(connectionFactory);
        return jmsComponent;
    }
}