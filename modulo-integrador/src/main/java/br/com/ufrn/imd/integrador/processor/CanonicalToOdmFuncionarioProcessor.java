package br.com.ufrn.imd.integrador.processor;

import br.com.ufrn.imd.integrador.model.FuncionarioCanonical;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("canonicalToOdmFuncionarioProcessor")
public class CanonicalToOdmFuncionarioProcessor implements Processor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {
        FuncionarioCanonical canonical = exchange.getIn().getBody(FuncionarioCanonical.class);

        Map<String, Object> odmFuncionario = new HashMap<>();
        odmFuncionario.put("nome", canonical.getNome());
        odmFuncionario.put("dataDeNascimento", canonical.getDataDeNascimento());
        odmFuncionario.put("salario", canonical.getSalario());

        String odmJson = objectMapper.writeValueAsString(odmFuncionario);
        System.out.println("JSON a ser enviado para ODM: " + odmJson);
        exchange.getIn().setBody(odmJson);
    }
}