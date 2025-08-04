package br.com.orm.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class FuncionarioProducerService {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${fila.funcionarios}")
    private String nomeFila;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void enviarFuncionario(Object funcionario) {
        try {
            String funcionarioJson = objectMapper.writeValueAsString(funcionario);
            jmsTemplate.convertAndSend(nomeFila, funcionarioJson);
            System.out.println("Mensagem enviada para a fila: " + funcionarioJson);
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao serializar funcionário para JSON: " + e.getMessage());
        }
    }
}