package br.com.ufrn.imd.integrador.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("httpSenderProcessor")
public class HttpSenderProcessor implements Processor {

    @Value("${odm.service.url}")
    private String odmUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void process(Exchange exchange) throws Exception {
        String jsonBody = exchange.getIn().getBody(String.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        System.out.println("Enviando para URL: " + odmUrl);
        System.out.println("Headers: " + headers);
        System.out.println("Body: " + jsonBody);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(odmUrl, request, String.class);
            exchange.getIn().setBody(response.getBody());
            System.out.println("Resposta recebida: " + response.getStatusCode() + " - " + response.getBody());
        } catch (Exception e) {
            System.err.println("Erro ao enviar para ODM: " + e.getMessage());
            throw e;
        }
    }
}