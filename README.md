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
---




