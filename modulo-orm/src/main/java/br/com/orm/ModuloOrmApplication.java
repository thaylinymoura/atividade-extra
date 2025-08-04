package br.com.orm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class ModuloOrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuloOrmApplication.class, args);
    }

}
