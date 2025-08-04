# Atividade Extra
## Aluna: [Thayliny Moura]

# Projeto de Integração de Sistemas: ORM, ODM e Apache Camel


## Tecnologias utilizadas 

**Java 17 ou superior**
**Apache Maven 3.8 ou superior**
**Docker (ou uma instância local do MongoDB rodando na porta 27017)**


Este projeto demonstra um cenário de integração de sistemas utilizando o **Apache Camel** como orquestrador. O objetivo é integrar duas camadas de persistência distintas:

- **Módulo ORM**: Uma aplicação Spring Boot que utiliza Spring Data JPA com um banco de dados H2 (em memória) para gerenciar uma entidade `Funcionario`. Atua como o sistema de origem.
- **Módulo ODM**: Uma aplicação Spring Boot que utiliza Spring Data MongoDB para gerenciar a mesma entidade `Funcionario`. Atua como o sistema de destino.
- **Módulo Integrador**: Uma aplicação Spring Boot com Apache Camel, responsável por consumir as atualizações do Módulo ORM, transformá-las e enviá-las para o Módulo ODM.

O fluxo de integração é acionado sempre que um funcionário é criado ou atualizado no Módulo ORM. A comunicação entre o ORM e o Integrador é feita de forma **assíncrona** via mensageria (JMS com ActiveMQ), e a comunicação entre o Integrador e o ODM é feita de forma **síncrona** via REST.

---

##  Conceitos e Padrões de Integração

Este projeto aplica os seguintes **padrões de integração** (Enterprise Integration Patterns):

- **Message Channel**: A fila JMS (`funcionarios.fila`) atua como um canal de mensagens, desacoplando o produtor (ORM) do consumidor (Integrador).
- **Publish-Subscribe Channel**: Embora implementado com uma fila (ponto-a-ponto), o conceito pode ser estendido para um tópico para ter múltiplos consumidores. O Módulo ORM publica a informação sem saber quem irá consumi-la.
- **Message Translator (Processor)**: Temos dois processadores no Camel:
  - `OrmToCanonicalFuncionarioProcessor`: Traduz a mensagem do formato do Módulo ORM para um Modelo de Dados Canônico.
  - `CanonicalToOdmFuncionarioProcessor`: Traduz a mensagem do modelo canônico para o formato esperado pelo Módulo ODM.
- **Canonical Data Model**: A classe `FuncionarioCanonical` representa um modelo de dados neutro e padronizado, que desacopla os modelos específicos dos sistemas de origem e destino.
- **Routing (Content-Based Router)**: A rota Camel (`JmsToRestRouter`) define o fluxo da mensagem desde o consumo da fila até a entrega no serviço de destino.

---

## Modelagem

### Entidades

**Funcionario (ORM - `modulo-orm`)**
- `id`: Long (Chave primária, gerada automaticamente)
- `nome`: String
- `dataDeNascimento`: String
- `salario`: double

**Funcionario (ODM - `modulo-odm`)**
- `id`: String (ObjectID do MongoDB, gerado automaticamente)
- `nome`: String
- `dataDeNascimento`: String
- `salario`: double

**FuncionarioCanonical (Integrador - `modulo-integrador`)**
- `identificador`: String (ID do sistema de origem)
- `nome`: String
- `dataDeNascimento`: String
- `salario`: double
- `origem`: String (Informa o sistema de origem, ex: "ORM")

---

### Arquitetura

```text
+------------+
| Cliente    |
+------------+
      |
      | 1. POST /funcionarios (JSON)
      v
+----------------------------------------+
| Módulo ORM (localhost:8081)            |
| - Spring Boot + JPA + H2               |
| - Salva no H2                          |
| - Publica na fila "funcionarios"       |
+----------------------------------------+
      |
      | 2. Mensagem JMS (JSON do Funcionario ORM)
      v
+------------------------------------------------------+
| Módulo Integrador (localhost:8083)                   |
| - Apache Camel                                       |
| - Consome da fila                                    |
| - Processador: ORM -> Canônico                       |
| - Processador: Canônico -> ODM                       |
+------------------------------------------------------+
      |
      | 3. POST /funcionarios (JSON do Funcionario ODM)
      v
+----------------------------------------+
| Módulo ODM (localhost:8082)            |
| - Spring Boot + MongoDB                |
| - Salva no MongoDB                     |

+----------------------------------------+

```


## Compilação e Execução

### Passos

1. Clone o repositório.
2. Inicie o MongoDB (se usar Docker):

```bash
docker run -d -p 27017:27017 --name mongo-funcionarios mongo
```

3. Compile o projeto:

Navegue até a raiz do projeto (onde está o `pom.xml` principal) e execute:

```bash
mvn clean install
```

4. Execute os módulos em **três terminais diferentes**:

#### Terminal 1: Módulo ORM

```bash
cd modulo-orm
mvn spring-boot:run
```

> Este módulo também iniciará um broker ActiveMQ embutido na porta 61616.

#### Terminal 2: Módulo ODM

```bash
cd modulo-odm
mvn spring-boot:run
```

#### Terminal 3: Módulo Integrador

```bash
cd modulo-integrador
mvn spring-boot:run
```

---

## Testes e Exemplos de Execução

Com os três módulos rodando, você pode testar a integração.

### Exemplo 1: Criar um novo funcionário

Envie uma requisição **POST** para o Módulo ORM:

```bash
curl -X POST http://localhost:8081/funcionarios -H "Content-Type: application/json" -d '{
  "nome": "João Silva",
  "dataDeNascimento": "1990-01-15",
  "salario": 5500.00
}'
```

### Verifique o resultado no Módulo ODM:

Envie uma requisição **GET** para o Módulo ODM:

```bash
curl http://localhost:8082/funcionarios
```

A saída deverá ser uma lista JSON contendo o funcionário `"João Silva"`.

---




