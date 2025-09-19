# Documentação Técnica - Sistema E-Commerce

**Projeto:** Sistema E-Commerce Spring Boot
**Autor:** Evandro Giacomelli
**Versão:** 1.2
**Data:** Setembro 2025

---

## Índice

1. [Visão Geral do Sistema](#visão-geral-do-sistema)
2. [Arquitetura da Aplicação](#arquitetura-da-aplicação)
3. [Schema do Banco de Dados](#schema-do-banco-de-dados)
4. [Sistema de Notificações por Email](#sistema-de-notificações-por-email)
5. [Documentação da API (Swagger)](#documentação-da-api-swagger)
6. [Módulo Cliente](#módulo-cliente)
7. [Módulo Produto](#módulo-produto)
8. [Módulo Pedido](#módulo-pedido)
9. [Fluxo de Requisições](#fluxo-de-requisições)
10. [Tratamento de Erros](#tratamento-de-erros)
11. [Considerações de Performance](#considerações-de-performance)
12. [Estratégia de Testes](#estratégia-de-testes)

---

## Visão Geral do Sistema

Sistema e-commerce desenvolvido em Spring Boot. Gerencia três entidades principais: Clientes, Produtos e Pedidos, com gerenciamento completo do ciclo de vida dos pedidos.

### Funcionalidades Principais
- Cadastro e gerenciamento de clientes com validação de documentos e email
- Gestão do catálogo de produtos com controle de status
- Ciclo completo de pedidos (criação, gestão de itens, pagamento, entrega)
- Sistema de notificações automáticas por email durante o ciclo de vida dos pedidos
- Validação abrangente em todas as camadas
- Design de API RESTful com códigos HTTP apropriados
- Documentação automática da API via Swagger/OpenAPI 3.0
- Banco de dados H2 em memória para desenvolvimento

### Stack Tecnológica
- **Framework:** Spring Boot 3.5.5
- **Linguagem:** Java 17
- **Banco de Dados:** H2 (desenvolvimento), JPA/Hibernate
- **Arquitetura:** Arquitetura em camadas com domain-driven design
- **Email:** Spring Mail com suporte SMTP (MailerSend/EmailTrap)
- **Documentação:** SpringDoc OpenAPI 3.0 para Swagger
- **Validação:** Jakarta Validation com validadores customizados
- **Testes:** JUnit, Spring Boot Test, Mockito

---

## Arquitetura da Aplicação

A aplicação segue um padrão de arquitetura em 5 camadas:

```
┌─────────────────────────────────┐
│        Camada Controller        │ ← Endpoints HTTP, Manipulação Request/Response
├─────────────────────────────────┤
│        Camada Service           │ ← Lógica de negócio, Gerenciamento de transações
├─────────────────────────────────┤
│       Camada Validation         │ ← Validação de entrada, Regras de negócio
├─────────────────────────────────┤
│        Camada Domain            │ ← Entidades, Lógica de domínio, Gestão de estado
├─────────────────────────────────┤
│       Camada Repository         │ ← Acesso a dados, Operações JPA
└─────────────────────────────────┘
```

### Estrutura da Arquitetura
1. Separação de responsabilidades por camada
2. Lógica de negócio nas entidades
3. Inversão de dependências
4. Responsabilidade única por classe
5. Extensão sem modificação

---

## Sistema de Notificações por Email

### Arquitetura do Sistema de Email

O sistema de notificações é baseado em eventos do ciclo de vida dos pedidos, enviando emails automáticos para os clientes em momentos-chave do processo de compra.

```
Mudança de Status → EmailService.sendOrderUpdateEmail()
                      ↓
              MimeMessageHelper (HTML + UTF-8)
                      ↓
                 JavaMailSender
                      ↓
              Provedor SMTP (MailerSend/EmailTrap)
```

### Integração com Módulo de Pedidos

O sistema de email está integrado ao `OrderServiceImpl`, sendo acionado automaticamente durante as transições de estado dos pedidos:

**Triggers de Notificação:**
1. **Finalização do Pedido:** Status muda para `WAITING_PAYMENT`
2. **Pagamento Aprovado:** Status muda para `PAID`
3. **Entrega Confirmada:** Status muda para `FINISHED`

### Configuração SMTP

O sistema suporta provedores SMTP externos configurados via `application.properties`:

```properties
spring.mail.host=smtp.mailersend.net
spring.mail.port=587
spring.mail.username=MS_ojEVdI@test-51ndgwv61m5lzqx8.mlsender.net
spring.mail.password=mssp.lg8I5FX.351ndgwv61m5lzqx8.XHt7VC3
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Template de Email Responsivo

O template HTML é otimizado para:
- **Responsividade:** Design adaptável para dispositivos móveis e desktop
- **Simplicidade:** Layout limpo e profissional sem elementos desnecessários
- **Informatividade:** Dados essenciais do pedido apresentados claramente
- **Acessibilidade:** Cores contrastantes e fontes legíveis

**Estrutura do Template:**
```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>/* CSS responsivo e minimalista */</style>
</head>
<body>
    <div class="container">
        <div class="header">E-Commerce Store</div>
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

### Tratamento de Erros

O sistema de email inclui tratamento robusto de exceções:
- **MessagingException:** Erros de configuração SMTP ou envio
- **UnsupportedEncodingException:** Problemas de codificação UTF-8
- **Logging:** Registra tentativas de envio e falhas para debug
- **Graceful Degradation:** Falhas de email não interrompem o fluxo do pedido

---

## Documentação da API (Swagger)

### Integração SpringDoc OpenAPI 3.0

A aplicação inclui documentação automática e interativa da API através da integração com SpringDoc OpenAPI 3.0:

**Endpoints de Documentação:**
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/api-docs`
- **Configuração:** Definida em `SwaggerConfig.java`

### Configuração do Swagger

```java
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Commerce API")
                        .description("Spring Boot E-Commerce REST API with JPA and Email Notifications")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Suporte Técnico")
                                .email("suporte@exemplo.com")));
    }
}
```

### Recursos da Documentação Interativa

**Interface Swagger UI:**
- **Exploração de Endpoints:** Listagem completa de todos os endpoints da API
- **Testes Interativos:** Possibilidade de testar endpoints diretamente na interface
- **Esquemas de Request/Response:** Documentação detalhada dos formatos de dados
- **Códigos de Status HTTP:** Documentação completa dos códigos de resposta
- **Modelos de Dados:** Visualização dos DTOs e estruturas de dados

**Configuração Personalizada:**
```properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operations-sorter=method
```

### Benefícios da Documentação Automática

1. **Sincronização Automática:** Documentação sempre atualizada com o código
2. **Testes Interativos:** Desenvolvedores podem testar endpoints sem ferramentas externas
3. **Padrão OpenAPI:** Compatibilidade com ferramentas de geração de cliente
4. **Facilita Integração:** Equipes frontend e mobile têm referência completa da API
5. **Reduz Overhead:** Elimina necessidade de manter documentação separada

---

## Schema do Banco de Dados

### Design do Schema

Quatro tabelas principais com relacionamentos:

```sql
-- Tabela de clientes com endereço e informações de registro incorporados
CREATE TABLE customers (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birth_date DATE,
    cpf VARCHAR(255) UNIQUE NOT NULL,
    rg VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    zip_code VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    number INTEGER,
    register_date TIMESTAMP NOT NULL,
    last_access TIMESTAMP,
    status TINYINT CHECK (status BETWEEN 0 AND 2),
    inactive_in TIMESTAMP
);

-- Tabela do catálogo de produtos
CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    status ENUM('ACTIVE','INACTIVE') NOT NULL
);

-- Tabela cabeçalho do pedido
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    status ENUM('OPEN','WAITING_PAYMENT','PAID','FINISHED','CANCELLED') NOT NULL,
    payment_status ENUM('PENDING','APPROVED','REJECTED','REFUNDED') NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Tabela de itens do pedido
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

### Relacionamentos das Entidades
- **Cliente → Pedido:** Um-para-Muitos (um cliente pode ter múltiplos pedidos)
- **Pedido → ItemPedido:** Um-para-Muitos com operações em cascata
- **Produto → ItemPedido:** Muitos-para-Um (mesmo produto pode estar em múltiplos pedidos)

---

## Módulo Cliente

O módulo Cliente gerencia o cadastro de clientes, gestão de perfil e controle de status.

### Componentes Principais
- `CustomerController`: Endpoints REST
- `CustomerServiceImpl`: Lógica de negócio e validação
- `Customer`: Entidade de domínio com objetos incorporados
- `CustomerDocuments`, `CustomerAddress`, `CustomerRegisterInfo`: Objetos de valor
- Validadores customizados para documentos e endereço

### Endpoints da API

#### POST /customers - Cadastrar Novo Cliente
**Requisição:**
```json
{
    "name": "João Silva",
    "birthDate": "1990-05-15",
    "cpf": "123.456.789-00",
    "rg": "12345678",
    "email": "joao@email.com",
    "zipCode": "01234-567",
    "street": "Rua das Flores",
    "number": 123
}
```

**Fluxo do Processo:**
1. Controller recebe e valida formato da requisição
2. Converte DTO para objetos de domínio (Documentos, Endereço, InfoRegistro)
3. Valida formato dos documentos (regex CPF, campos obrigatórios)
4. Valida completude do endereço
5. Verifica duplicatas de CPF, RG e email no banco
6. Cria entidade Customer com status ATIVO
7. Persiste no banco com UUID gerado
8. Retorna dados do cliente com 201 CREATED

**Regras de Validação:**
- Nome: Obrigatório, não vazio
- CPF: Obrigatório, formato `\d{3}\.\d{3}\.\d{3}-\d{2}`
- RG: Obrigatório, não vazio
- Email: Obrigatório e deve ser único (necessário para notificações)
- Endereço: CEP e Rua obrigatórios
- Unicidade: CPF, RG e email devem ser únicos em todos os clientes

#### GET /customers/{id} - Buscar Cliente por ID
Retorna detalhes do cliente ou 404 se não encontrado.

#### PUT /customers/{id} - Atualizar Cliente
Atualização completa das informações do cliente com as mesmas validações da criação, exceto permite manter valores únicos existentes para o mesmo cliente.

#### PATCH /customers/{id}/deactivate - Desativar Cliente
Muda status para INATIVO e define timestamp inactiveIn.

#### PATCH /customers/{id}/activate - Ativar Cliente
Muda status de volta para ATIVO e limpa timestamp inactiveIn.

### Regras de Negócio
1. Apenas clientes ativos podem criar pedidos
2. CPF, RG e email devem ser únicos no sistema
3. Status do cliente afeta capacidade de criação de pedidos
4. Informações de endereço e documentos são incorporadas (não tabelas separadas)

---

## Módulo Produto

O módulo Produto gerencia o catálogo de produtos com controle de status e preços.

### Componentes Principais
- `ProductController`: Endpoints REST
- `ProductServiceImpl`: Lógica de negócio com validação
- `Product`: Entidade de domínio com campos de auditoria
- Validação de preços e gerenciamento de status

### Endpoints da API

#### POST /products - Criar Produto
**Requisição:**
```json
{
    "name": "MacBook Pro M3",
    "description": "Mais recente MacBook Pro com chip M3",
    "price": 2499.99
}
```

**Fluxo do Processo:**
1. Extrai dados do produto da requisição
2. Valida se nome e descrição não estão vazios
3. Valida se preço é positivo
4. Cria entidade Product com status ATIVO
5. Define timestamps de criação e atualização
6. Salva no banco com UUID gerado
7. Retorna dados do produto com 201 CREATED

#### GET /products/{id} - Obter Produto por ID
Retorna detalhes do produto ou 404 se não encontrado.

#### GET /products - Listar Todos os Produtos
Retorna todos os produtos independente do status.

#### GET /products/active - Listar Apenas Produtos Ativos
Retorna apenas produtos com status ATIVO usando query customizada.

#### PUT /products/{id} - Atualizar Produto
Atualização completa com validação, atualiza automaticamente timestamp updatedAt.

#### PATCH /products/{id}/deactivate - Desativar Produto
Define status para INATIVO, impede que produto apareça em listagens ativas.

#### PATCH /products/{id}/activate - Ativar Produto
Define status para ATIVO, torna produto disponível novamente.

### Regras de Negócio
1. Nome e descrição do produto não podem estar vazios
2. Preço deve ser positivo
3. Apenas produtos ativos devem ser mostrados aos clientes
4. Timestamp de atualização é gerenciado automaticamente
5. Produtos desativados permanecem no sistema para histórico de pedidos

---

## Módulo Pedido

O módulo Pedido é o mais complexo, gerenciando o ciclo de vida completo do pedido com gestão de estado e operações de itens.

### Gestão de Estado

O sistema de pedidos usa uma abordagem de estado duplo:

**Status do Pedido:**
- `OPEN`: Estado inicial, permite modificações de itens
- `WAITING_PAYMENT`: Pedido finalizado, aguardando pagamento
- `PAID`: Pagamento concluído, pronto para entrega
- `FINISHED`: Pedido entregue e concluído
- `CANCELLED`: Pedido cancelado

**Status do Pagamento:**
- `PENDING`: Padrão para novos pedidos
- `APPROVED`: Pagamento processado com sucesso
- `REJECTED`: Pagamento falhou ou pedido cancelado
- `REFUNDED`: Pagamento reembolsado (não usado atualmente)

### Endpoints da API

#### POST /orders - Criar Pedido
**Requisição:**
```json
{
    "customerId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Fluxo do Processo:**
1. Valida se cliente existe e está ativo
2. Cria novo Pedido com status OPEN e pagamento PENDING
3. Inicializa lista vazia de itens
4. Salva no banco com timestamp atual
5. Retorna dados do pedido com 201 CREATED

#### POST /orders/{orderId}/items - Adicionar Item ao Pedido
**Requisição:**
```json
{
    "productId": "550e8400-e29b-41d4-a716-446655440001",
    "quantity": 2,
    "salePrice": 2399.99
}
```

**Fluxo do Processo:**
1. Valida se pedido existe e está em status OPEN
2. Valida se produto existe
3. Verifica se item já existe no pedido:
   - Se existe: Adiciona à quantidade existente
   - Se novo: Cria novo OrderItem
4. Valida quantidade > 0 e salePrice > 0
5. Salva pedido com itens atualizados
6. Retorna dados atualizados do pedido

**Regras de Negócio:**
- Apenas pedidos OPEN podem ter itens modificados
- Produtos duplicados incrementam quantidade existente
- Quantidade e preço de venda devem ser positivos
- Itens mantêm relacionamento bidirecional com pedido

#### DELETE /orders/{orderId}/items/{productId} - Remover Item
Remove produto específico do pedido, apenas permitido em status OPEN.

#### PUT /orders/{orderId}/items/{productId} - Atualizar Quantidade do Item
Atualiza quantidade para item específico, remove item se quantidade ficar 0.

#### PATCH /orders/{orderId}/finalize - Finalizar Pedido
**Fluxo do Processo:**
1. Valida se pedido está em status OPEN
2. Valida se pedido tem pelo menos um item
3. Valida se valor total > 0
4. Muda status para WAITING_PAYMENT
5. **Envia email de confirmação para o cliente**
6. Pedido torna-se somente leitura para itens

**Regras de Validação:**
- Pedido deve estar OPEN
- Deve ter pelo menos um item
- Valor total deve ser maior que zero
- Após finalização, modificações de itens não são permitidas

#### PATCH /orders/{orderId}/pay - Processar Pagamento
**Fluxo do Processo:**
1. Valida se pedido está em status WAITING_PAYMENT
2. Muda status de pagamento para APPROVED
3. Muda status do pedido para PAID
4. **Envia email de pagamento aprovado para o cliente**

#### PATCH /orders/{orderId}/deliver - Entregar Pedido
**Fluxo do Processo:**
1. Valida se pedido está em status PAID
2. Muda status do pedido para FINISHED
3. **Envia email de entrega confirmada para o cliente**

#### PATCH /orders/{orderId}/cancel - Cancelar Pedido
**Fluxo do Processo:**
1. Valida se pedido não está FINISHED
2. Valida se pagamento não está APPROVED
3. Define status do pedido como CANCELLED
4. Define status do pagamento como REJECTED

**Regras de Negócio:**
- Não pode cancelar pedidos FINISHED
- Não pode cancelar pedidos com pagamentos APPROVED
- Cancelamento afeta tanto status do pedido quanto do pagamento

### Cálculo do Total do Pedido
O sistema calcula totais dos pedidos dinamicamente:
```java
public BigDecimal getTotalValue() {
    return items.stream()
        .map(OrderItem::getSubtotal)  // quantidade × preçoVenda
        .reduce(BigDecimal.ZERO, BigDecimal::add);
}
```

---

## Fluxo de Requisições

### Cenário Completo de Criação de Pedido

Vamos rastrear um fluxo de negócio típico: cadastro de cliente, criação de produto, processamento de pedido.

**Passo 1: Criar Cliente**
```
POST /customers → CustomerController.createCustomer()
    ↓
CustomerServiceImpl.createCustomer() [@Transactional]
    ↓
DocumentsValidator.validate() + AddressValidator.validate()
    ↓
validateUniqueDocuments() [Verifica unicidade CPF/RG/Email]
    ↓
new Customer(documents, address, registerInfo)
    ↓
customerRepository.save() [INSERT na tabela customers]
    ↓
Retorna CustomerResponse com UUID
```

**Passo 2: Adicionar Item ao Pedido Existente**
```
POST /orders/{id}/items → OrderController.addItemToOrder()
    ↓
OrderServiceImpl.addItemToOrder()
    ↓
orderRepository.findById() [SELECT pedido com dados do cliente]
    ↓
productService.findProductById() [SELECT dados do produto]
    ↓
order.addItem(product, quantity, salePrice) [Lógica de domínio]
    ↓
Validação regra de negócio: order.status == OPEN
    ↓
Verifica item existente com mesmo produto
    ↓
Cria novo OrderItem ou atualiza quantidade existente
    ↓
orderRepository.save() [UPDATE pedido, INSERT/UPDATE order_items]
    ↓
Retorna OrderResponse atualizado
```

### Exemplo de Transição de Estado

**Processo de Finalização do Pedido:**
```
PATCH /orders/{id}/finalize → OrderController.finalizeOrder()
    ↓
OrderServiceImpl.finalizeOrder()
    ↓
order.finalizeOrder() [Máquina de estado do domínio]
    ↓
Cadeia de validação:
    - status == OPEN ✓
    - !items.isEmpty() ✓
    - getTotalValue() > 0 ✓
    ↓
status = WAITING_PAYMENT [Transição de estado]
    ↓
orderRepository.save() [UPDATE orders SET status = 'WAITING_PAYMENT']
    ↓
Retorna OrderResponse atualizado
```

---

## Tratamento de Erros

A aplicação implementa tratamento abrangente de erros com códigos HTTP específicos e mensagens significativas.

### Hierarquia de Exceções

```
RuntimeException
├── Domínio Cliente
│   ├── CustomerNotFoundException (404)
│   └── InvalidCustomerDataException (400)
│       ├── DuplicateCpfException
│       ├── DuplicateRgException
│       └── DuplicateEmailException
├── Domínio Produto
│   ├── ProductNotFoundException (404)
│   └── InvalidProductDataException (400)
│       └── InvalidProductPriceException
└── Domínio Pedido
    ├── OrderNotFoundException (404)
    └── InvalidOrderDataException (400)
```

### Formato de Resposta de Erro

Todos os erros retornam uma estrutura JSON consistente:
```json
{
    "message": "CPF 123.456.789-00 já está cadastrado"
}
```

### Tratamento de Erro no Nível Controller

Cada controller trata exceções apropriadamente:

```java
public ResponseEntity<Object> createCustomer(@RequestBody CustomerRequest request) {
    try {
        CustomerResponse customer = customerService.createCustomer(creationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    } catch (InvalidCustomerDataException e) {
        return ResponseEntity.badRequest()
            .body(new ErrorMessage(e.getMessage()));
    }
}
```

### Estratégia de Códigos de Status HTTP

- **200 OK**: Operações GET bem-sucedidas
- **201 CREATED**: Criação bem-sucedida de recurso
- **400 BAD REQUEST**: Erros de validação, violações de regras de negócio
- **404 NOT FOUND**: Recurso não encontrado
- **500 INTERNAL SERVER ERROR**: Erros inesperados do sistema (tratado pelo Spring Boot)

---

## Considerações de Performance

### Configuração JPA
- **Lazy Loading**: Entidades relacionadas carregadas sob demanda para reduzir uso de memória
- **Operações Cascade**: OrderItems gerenciados através do ciclo de vida do Order
- **Chaves Primárias UUID**: Amigáveis para distribuição, sem contenção de sequência
- **Gerenciamento de Transação**: `@Transactional` garante consistência de dados

### Otimização de Queries
- **JPQL Customizada**: Queries otimizadas para casos de uso específicos como produtos ativos
- **Campos Indexados**: Restrições únicas fornecem indexação automática
- **Carregamento Seletivo**: DTOs contêm apenas campos necessários

### Design do Banco de Dados
- **Objetos Incorporados**: Endereço e documentos do cliente armazenados em tabela única
- **Restrições Adequadas**: Aplicação de regras de negócio no nível do banco
- **Precisão Decimal**: Valores monetários usam DECIMAL(10,2) para precisão

### Gerenciamento de Memória
- **Design Stateless**: Nenhum estado de sessão mantido entre requisições
- **Padrão DTO**: Separação entre camadas de persistência e apresentação
- **Camadas de Validação**: Validação precoce previne processamento desnecessário

---

## Estratégia de Testes

A aplicação mantém mais de 156 testes organizados em múltiplos níveis:

### Testes Unitários
- **Camada Service**: Validação da lógica de negócio
- **Entidades de Domínio**: Transições de estado e regras de negócio
- **Validadores**: Lógica de validação de entrada

### Testes de Integração
- **Camada Repository**: Operações de banco de dados
- **Integração Controller**: Ciclos completos de requisição/resposta HTTP
- **Testes de Transação**: Operações multi-entidade

### Exemplos de Teste

```java
@Test
void devecriarClienteComDadosValidos() {
    // Given
    CustomerCreationRequest request = criarRequestValido();
    when(customerRepository.existsByCpf(anyString())).thenReturn(false);

    // When
    CustomerResponse response = customerService.createCustomer(request);

    // Then
    assertThat(response.getId()).isNotNull();
    assertThat(response.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
}

@Test
void deveLancarExcecaoParaCpfDuplicado() {
    // Given
    when(customerRepository.existsByCpf("123.456.789-00")).thenReturn(true);

    // When & Then
    assertThatThrownBy(() -> customerService.createCustomer(request))
        .isInstanceOf(DuplicateCpfException.class)
        .hasMessage("CPF 123.456.789-00 já está cadastrado");
}
```

### Áreas de Cobertura de Teste
1. **Caminho Feliz**: Todas as operações bem-sucedidas
2. **Falhas de Validação**: Tratamento de entrada inválida
3. **Regras de Negócio**: Aplicação da lógica de domínio
4. **Transições de Estado**: Gerenciamento do ciclo de vida do pedido
5. **Cenários de Erro**: Tratamento de exceções e mensagens

---

## Melhorias Futuras

### Melhorias de Segurança
- Adicionar autenticação e autorização (Spring Security)
- Implementar limitação de taxa da API
- Adicionar sanitização de validação de requisições
- Habilitar HTTPS em produção

### Otimizações de Performance
- Implementar camada de cache (Redis)
- Adicionar pool de conexões do banco
- Otimizar queries com paginação
- Adicionar processamento assíncrono para operações pesadas

### Extensões de Funcionalidade
- Integração com gateway de pagamento
- Sistema de gestão de estoque
- Rastreamento de pedidos e notificações
- Programas de fidelidade do cliente
- Avaliações e classificações de produtos

### Considerações de Escalabilidade
- Migrar para banco de produção (PostgreSQL)
- Implementar arquitetura de microsserviços
- Adicionar estratégia de versionamento da API
- Implementar event sourcing para eventos de pedidos

---

## Conclusão

Sistema e-commerce com arquitetura em camadas, validação completa e tratamento de erros. Adequado para operações de pequeno a médio porte.

Design modular permite manutenção e extensão fáceis. Tratamento de erros e validação fornecem experiência robusta. Estratégia de testes garante confiabilidade.

---

*Documentação técnica do sistema e-commerce.*