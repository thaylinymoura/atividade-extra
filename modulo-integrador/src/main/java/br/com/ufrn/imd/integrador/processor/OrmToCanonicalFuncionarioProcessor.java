package br.com.ufrn.imd.integrador.processor;


import br.com.ufrn.imd.integrador.model.FuncionarioCanonical;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("ormToCanonicalFuncionarioProcessor")
public class OrmToCanonicalFuncionarioProcessor implements Processor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {
        String jsonBody = exchange.getIn().getBody(String.class);
        Map<String, Object> ormFuncionario = objectMapper.readValue(jsonBody, Map.class);

        FuncionarioCanonical canonical = new FuncionarioCanonical();
        canonical.setIdentificador(ormFuncionario.get("id").toString());
        canonical.setNome((String) ormFuncionario.get("nome"));
        canonical.setDataDeNascimento((String) ormFuncionario.get("dataDeNascimento"));
        canonical.setSalario((Double) ormFuncionario.get("salario"));
        canonical.setOrigem("ORM");

        exchange.getIn().setBody(canonical);
    }
}
