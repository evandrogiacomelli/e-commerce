# README - Sistema E-Commerce

**Projeto:** Sistema E-Commerce Spring Boot
**Versão:** 1.0
**Data:** Setembro 2025

---

## Índice

1. [Visão Geral do Sistema](#visão-geral-do-sistema)
2. [Arquitetura da Aplicação](#arquitetura-da-aplicação)
3. [Stack Tecnológica](#stack-tecnológica)
4. [Configuração e Execução](#configuração-e-execução)
5. [Documentação da API](#documentação-da-api)
6. [Módulos do Sistema](#módulos-do-sistema)
7. [Sistema de Notificações](#sistema-de-notificações)
8. [Banco de Dados](#banco-de-dados)
9. [Estratégia de Testes](#estratégia-de-testes)
10. [Considerações de Deployment](#considerações-de-deployment)

---

## Visão Geral do Sistema

Sistema e-commerce desenvolvido em Spring Boot. Gerencia clientes, produtos e pedidos com notificações automáticas por email durante o ciclo de vida dos pedidos.

### Funcionalidades Implementadas
- Cadastro e gerenciamento completo de clientes com validação de documentos brasileiros
- Gestão do catálogo de produtos com controle de status e versionamento
- Ciclo completo de pedidos desde criação até entrega
- Sistema de notificações por email com templates responsivos
- Documentação automática da API via Swagger/OpenAPI 3.0
- Validação abrangente em todas as camadas da aplicação
- Tratamento padronizado de exceções e códigos de resposta HTTP

### Estrutura
- Arquitetura em camadas (Controller, Service, Repository)
- Endpoints REST padronizados
- Transações com rollback automático

---

## Arquitetura da Aplicação

Estrutura em 5 camadas:

```
┌─────────────────────────────────┐
│     Camada Controller           │ ← REST Endpoints, Validação de Request, Swagger
├─────────────────────────────────┤
│     Camada Service              │ ← Lógica de Negócio, Gerenciamento Transacional
├─────────────────────────────────┤
│     Camada Validation           │ ← Validação de Domínio, Regras de Negócio
├─────────────────────────────────┤
│     Camada Domain               │ ← Entidades JPA, Gestão de Estado, Lógica de Domínio
├─────────────────────────────────┤
│     Camada Repository           │ ← Spring Data JPA, Operações de Persistência
└─────────────────────────────────┘
```

### Estrutura Modular do Projeto

```
src/main/java/com/evandro/e_commerce/
├── config/                       # Configurações do sistema
│   └── SwaggerConfig.java        # Configuração OpenAPI/Swagger
├── customer/                     # Módulo Cliente
│   ├── controller/               # REST Controllers
│   ├── service/                  # Lógica de negócio
│   ├── repository/               # Repositórios JPA
│   ├── model/                    # Entidades de domínio
│   ├── dto/                      # Data Transfer Objects
│   ├── validation/               # Validadores customizados
│   └── exception/                # Exceções específicas
├── product/                      # Módulo Produto (estrutura similar)
├── order/                        # Módulo Pedido (estrutura similar)
└── notification/                 # Sistema de Notificações
    └── service/                  # Serviços de email
```

---

## Stack Tecnológica

### Framework Principal
- **Spring Boot 3.5.5**
- **Java 17**
- **Maven 3.6+**

### Dependências Spring
- **Spring Web** - APIs REST
- **Spring Data JPA** - Persistência
- **Spring Mail** - Envio de emails
- **Spring Validation** - Validação de dados
- **Spring Session JDBC** - Sessões

### Banco de Dados e Persistência
- **H2 Database** - Banco em memória
- **Hibernate/JPA** - ORM
- **Jakarta Validation** - Validações

### Documentação e Testes
- **SpringDoc OpenAPI 3** - Documentação da API
- **JUnit 5** - Testes unitários
- **Mockito** - Mocks
- **Spring Boot Test** - Testes integrados

### Comunicação Externa
- **JavaMail** - Envio de emails
- **SMTP Integration** - MailerSend/EmailTrap

---

## Configuração e Execução

### Pré-requisitos
- JDK 17+
- Maven 3.6+
- 512MB RAM

### Processo de Instalação

1. **Clonagem do Repositório:**
   ```bash
   git clone <repository-url>
   cd e-commerce
   ```

2. **Compilação e Execução:**
   ```bash
   # Compilar o projeto
   ./mvnw clean compile

   # Executar testes
   ./mvnw test

   # Executar aplicação
   ./mvnw spring-boot:run
   ```

3. **Verificação da Instalação:**
   - Aplicação executando em `http://localhost:8080`
   - Console H2 disponível em `http://localhost:8080/h2-console`
   - Documentação Swagger em `http://localhost:8080/swagger-ui.html`

### Configuração do Banco de Dados H2

```properties
# Configurações H2 (src/main/resources/application.properties)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
```

### Configuração do Sistema de Email

```properties
# Configuração SMTP para MailerSend/EmailTrap
spring.mail.host=smtp.mailersend.net
spring.mail.port=587
spring.mail.username=<seu_usuario>
spring.mail.password=<sua_senha>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## Documentação da API

### Swagger/OpenAPI Integration

A aplicação inclui documentação automática e interativa da API:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/api-docs`
- **Configuração:** Definida em `SwaggerConfig.java`

### Padronização de Responses

Todas as respostas da API seguem padrões HTTP consistentes:

- **200 OK:** Operações de consulta bem-sucedidas
- **201 CREATED:** Criação de recursos com sucesso
- **400 BAD REQUEST:** Erros de validação ou regras de negócio
- **404 NOT FOUND:** Recurso solicitado não encontrado
- **500 INTERNAL SERVER ERROR:** Erros inesperados do sistema

### Formato de Erro Padronizado

```json
{
    "message": "Descrição específica do erro"
}
```

---

## Módulos do Sistema

### Módulo Cliente (Customer)

**Responsabilidades:**
- Cadastro e manutenção de dados pessoais
- Validação de documentos brasileiros (CPF, RG)
- Gestão de endereços incorporados
- Controle de status de ativação

**Endpoints Principais:**
```
POST   /customers              - Criar novo cliente
GET    /customers/{id}         - Buscar cliente por ID
GET    /customers/active       - Listar clientes ativos
PUT    /customers/{id}         - Atualizar dados do cliente
PATCH  /customers/{id}/activate - Ativar cliente
```

**Regras de Validação:**
- CPF deve seguir padrão brasileiro e ser único
- RG obrigatório e único no sistema
- Email opcional mas deve ser único quando fornecido
- Endereço com CEP e logradouro obrigatórios

### Módulo Produto (Product)

**Responsabilidades:**
- Gestão do catálogo de produtos
- Controle de preços e descrições
- Versionamento com timestamps
- Gestão de status (ATIVO/INATIVO)

**Endpoints Principais:**
```
POST   /products               - Criar novo produto
GET    /products/{id}          - Buscar produto por ID
GET    /products/active        - Listar produtos ativos
PUT    /products/{id}          - Atualizar produto
```

**Regras de Negócio:**
- Preço deve ser positivo
- Nome e descrição obrigatórios
- Timestamps de criação e atualização gerenciados automaticamente

### Módulo Pedido (Order)

**Responsabilidades:**
- Gestão completa do ciclo de vida dos pedidos
- Controle de estados duplo (pedido + pagamento)
- Gerenciamento de itens com cálculos automáticos
- Integração com sistema de notificações

**Estados do Pedido:**
- `OPEN` - Permite modificações de itens
- `WAITING_PAYMENT` - Aguardando processamento do pagamento
- `PAID` - Pagamento aprovado, pronto para envio
- `FINISHED` - Pedido entregue e concluído
- `CANCELLED` - Pedido cancelado

**Estados do Pagamento:**
- `PENDING` - Estado inicial
- `APPROVED` - Pagamento processado com sucesso
- `REJECTED` - Pagamento negado ou pedido cancelado
- `REFUNDED` - Reembolso processado

**Endpoints de Gestão de Pedidos:**
```
POST   /orders                           - Criar pedido
POST   /orders/{id}/items               - Adicionar item
DELETE /orders/{id}/items/{productId}   - Remover item
PATCH  /orders/{id}/finalize            - Finalizar pedido
PATCH  /orders/{id}/pay                 - Processar pagamento
PATCH  /orders/{id}/deliver             - Marcar como entregue
```

---

## Sistema de Notificações

### Arquitetura do Sistema de Email

O sistema de notificações é baseado em eventos do ciclo de vida dos pedidos:

```
Mudança de Status → EmailService.sendOrderUpdateEmail()
                      ↓
              MimeMessageHelper (HTML + UTF-8)
                      ↓
                 JavaMailSender
                      ↓
              Provedor SMTP (MailerSend/EmailTrap)
```

### Triggers de Notificação

1. **Finalização do Pedido:** Status muda para `WAITING_PAYMENT`
2. **Pagamento Aprovado:** Status muda para `PAID`
3. **Entrega Confirmada:** Status muda para `FINISHED`

### Template de Email

O template HTML é otimizado para:
- **Responsividade:** Funciona em dispositivos móveis e desktop
- **Simplicidade:** Design limpo sem poluição visual
- **Informatividade:** Dados essenciais do pedido claramente apresentados
- **Acessibilidade:** Cores contrastantes e fontes legíveis

**Estrutura do Template:**
```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>/* CSS minimalista */</style>
</head>
<body>
    <div class="container">
        <div class="header"><!-- Cabeçalho da loja --></div>
        <h2>Olá, [NOME_CLIENTE]!</h2>
        <h3>Pedido #[ID_PEDIDO]</h3>
        <span class="status-badge">[STATUS]</span>
        <div class="total">Total: R$ [VALOR]</div>
        [LISTA_ITENS]
        <p>[MENSAGEM_STATUS]</p>
    </div>
</body>
</html>
```

---

## Banco de Dados

### Design do Schema

Quatro tabelas principais:

```sql
-- Clientes com dados incorporados
CREATE TABLE customers (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birth_date DATE,
    cpf VARCHAR(255) UNIQUE NOT NULL,
    rg VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE,
    zip_code VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    number INTEGER,
    register_date TIMESTAMP NOT NULL,
    last_access TIMESTAMP,
    status TINYINT CHECK (status BETWEEN 0 AND 2),
    inactive_in TIMESTAMP
);

-- Catálogo de produtos
CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    status ENUM('ACTIVE','INACTIVE') NOT NULL
);

-- Cabeçalho dos pedidos
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    status ENUM('OPEN','WAITING_PAYMENT','PAID','FINISHED','CANCELLED') NOT NULL,
    payment_status ENUM('PENDING','APPROVED','REJECTED','REFUNDED') NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Itens dos pedidos
CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    sale_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

### Otimizações

- Chaves UUID para distribuição
- Índices únicos em CPF, RG e email
- Relacionamentos cascade
- DECIMAL para valores monetários

---

## Estratégia de Testes

### Cobertura de Testes

156 testes organizados nas seguintes categorias:

**Testes Unitários:**
- Validação de regras de negócio na camada Service
- Comportamento das entidades de domínio
- Lógica de validadores customizados
- Transições de estado dos pedidos

**Testes de Integração:**
- Operações completas de Controller
- Persistência via Repository
- Transações multi-entidade
- Integração com sistema de email (mocked)

### Estrutura de Testes

```
src/test/java/com/evandro/e_commerce/
├── customer/
│   ├── controller/    # Testes de endpoints HTTP
│   ├── service/       # Testes de lógica de negócio
│   └── model/         # Testes de entidades
├── product/           # Estrutura similar
└── order/             # Estrutura similar
```

### Comandos de Execução

```bash
# Executar todos os testes
./mvnw test

# Testes específicos por módulo
./mvnw test -Dtest="*Customer*"
./mvnw test -Dtest="*Product*"
./mvnw test -Dtest="*Order*"

# Testes por camada
./mvnw test -Dtest="*Controller*"
./mvnw test -Dtest="*Service*"

# Testes com relatório de cobertura
./mvnw clean test
```

---

## Considerações de Deployment

### Ambiente de Desenvolvimento

A aplicação está configurada para execução local com:
- Banco H2 em memória (dados perdidos ao reiniciar)
- Console H2 habilitado para debug
- Logs SQL habilitados para desenvolvimento
- Configuração SMTP para EmailTrap/MailerSend

### Preparação para Produção

Para deployment em produção, considerar:

**Banco de Dados:**
- Migrar para PostgreSQL ou MySQL
- Configurar pool de conexões otimizado
- Implementar estratégia de backup

**Segurança:**
- Adicionar Spring Security com JWT
- Implementar rate limiting
- Configurar HTTPS obrigatório

**Performance:**
- Adicionar cache Redis para queries frequentes
- Implementar paginação em listagens
- Configurar profiles específicos por ambiente

**Monitoramento:**
- Integrar Spring Actuator para health checks
- Configurar logging estruturado
- Implementar métricas de negócio

### Configurações de Profile

```properties
# application-prod.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
spring.jpa.hibernate.ddl-auto=validate
spring.h2.console.enabled=false
logging.level.org.hibernate.SQL=WARN
```

---