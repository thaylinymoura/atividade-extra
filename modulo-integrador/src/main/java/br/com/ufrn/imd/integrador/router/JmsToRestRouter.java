package br.com.ufrn.imd.integrador.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class JmsToRestRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("jms:{{fila.funcionarios}}")
                .routeId("rota-funcionarios-orm-para-odm")
                .log("Recebida mensagem da fila: ${body}")
                .process("ormToCanonicalFuncionarioProcessor")
                .log("Mensagem transformada para modelo canônico: ${body}")
                .process("canonicalToOdmFuncionarioProcessor")
                .log("Mensagem transformada para modelo ODM: ${body}")
                .process("httpSenderProcessor")
                .log("Mensagem enviada para o serviço ODM. Resposta: ${body}");
    }
}