# Ada Commerce - E-Commerce Project

## Visão Geral

Este é um projeto de e-commerce desenvolvido seguindo **Test-Driven Development (TDD)** e princípios **SOLID**. O objetivo é criar um sistema completo de vendas online com gestão de clientes, produtos e pedidos.

## Arquitetura do Projeto

### Estrutura de Packages

```
src/main/java/com/evandro/e_commerce/
├── customer/
│   ├── model/           # Entidades e Value Objects
│   ├── factory/         # Factory para criação com validação
│   ├── exception/       # Exceções específicas do domínio
│   └── service/         # Serviços de negócio (futuro)
├── product/             # Domínio de Produtos (a desenvolver)
│   ├── model/
│   ├── factory/
│   ├── exception/
│   └── service/
└── order/               # Domínio de Pedidos (a desenvolver)
    ├── model/
    ├── factory/
    ├── exception/
    └── service/
```

### Estrutura de Testes

```
src/test/java/com/evandro/e_commerce/
├── customer/
│   ├── model/           # Testes de comportamento das entidades
│   └── factory/         # Testes de validação e criação
├── product/             # Testes do domínio de produtos
└── order/               # Testes do domínio de pedidos
```

## Metodologia TDD Aplicada

### Ciclo Red-Green-Refactor

1. **Red**: Escrever teste que falha
2. **Green**: Implementar código mínimo para passar o teste
3. **Refactor**: Melhorar o código mantendo os testes passando

### Exemplo Implementado - Customer Domain

#### 1. Model (Entidades e Value Objects)
- `Customer.java` - Agregado principal
- `CustomerDocuments.java` - Value Object para documentos
- `CustomerAddress.java` - Value Object para endereço
- `CustomerRegisterInfo.java` - Value Object para informações de registro

#### 2. Factory Pattern
- `CustomerFactory.java` - Responsável por validação e criação
- Valida dados antes da criação
- Lança exceções específicas do domínio

#### 3. Custom Exceptions
- `InvalidCustomerDataException.java` - Exceção base
- `InvalidCpfException.java` - Para CPF inválido
- `InvalidAddressException.java` - Para endereço inválido
- `InvalidRgException.java` - Para RG inválido

#### 4. Testes
- `CustomerTest.java` - Testa comportamento da entidade
- `CustomerFactoryTest.java` - Testa validações e criação

## Como Implementar o Domínio de Produtos

### Requisitos de Negócio para Product

1. **Cadastrar produtos** com nome, descrição e preço
2. **Listar e atualizar produtos** (não excluir - manter histórico)
3. **Produtos devem ter preço maior que zero**
4. **Nome do produto é obrigatório**

### Passo a Passo TDD para Product Domain

#### Passo 1: Criar Models (sem comportamento)

```java
// Product.java
package com.evandro.e_commerce.product.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Product {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProductStatus status;
    
    public Product(String name, String description, BigDecimal price) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ProductStatus.ACTIVE;
    }
    
    // Getters aqui
}

// ProductStatus.java
public enum ProductStatus {
    ACTIVE, INACTIVE
}
```

#### Passo 2: Escrever Primeiro Teste (FAIL)

```java
// ProductTest.java
@Test
@DisplayName("Should create product with valid data")
void shouldCreateProductWithValidData() {
    Product product = new Product("Smartphone", "iPhone 14", new BigDecimal("3000.00"));
    
    assertNotNull(product);
    assertNotNull(product.getId());
    assertEquals("Smartphone", product.getName());
    assertEquals(new BigDecimal("3000.00"), product.getPrice());
    assertEquals(ProductStatus.ACTIVE, product.getStatus());
}
```

#### Passo 3: Fazer o Teste Passar (GREEN)

Implementar getters necessários na classe Product.

#### Passo 4: Criar Factory com Validação

```java
// ProductFactory.java
public class ProductFactory {
    
    public static Product create(String name, String description, BigDecimal price) {
        validateInputs(name, description, price);
        return new Product(name, description, price);
    }
    
    private static void validateInputs(String name, String description, BigDecimal price) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidProductDataException("Product name cannot be null or empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidProductPriceException("Product price must be greater than zero");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidProductDataException("Product description cannot be null or empty");
        }
    }
}
```

#### Passo 5: Criar Exceções Customizadas

```java
// InvalidProductDataException.java
public class InvalidProductDataException extends RuntimeException {
    public InvalidProductDataException(String message) {
        super(message);
    }
}

// InvalidProductPriceException.java
public class InvalidProductPriceException extends InvalidProductDataException {
    public InvalidProductPriceException(String message) {
        super(message);
    }
}
```

#### Passo 6: Criar Testes do Factory

```java
// ProductFactoryTest.java
@Test
@DisplayName("Should throw exception when product name is null")
void shouldThrowExceptionWhenNameIsNull() {
    assertThrows(InvalidProductDataException.class, () -> {
        ProductFactory.create(null, "Description", new BigDecimal("100.00"));
    });
}

@Test
@DisplayName("Should throw exception when price is zero or negative")
void shouldThrowExceptionWhenPriceIsInvalid() {
    assertThrows(InvalidProductPriceException.class, () -> {
        ProductFactory.create("Product", "Description", BigDecimal.ZERO);
    });
}
```

## Comandos de Desenvolvimento

```bash
# Executar testes
./mvnw test

# Executar aplicação
./mvnw spring-boot:run

# Limpar e compilar
./mvnw clean compile
```

## Princípios SOLID Aplicados

- **SRP**: Cada classe tem uma responsabilidade única
- **OCP**: Extensível sem modificação (Factory pattern)
- **LSP**: Subtipos substituíveis (hierarquia de exceções)
- **ISP**: Interfaces pequenas e específicas
- **DIP**: Depende de abstrações, não implementações

## Next Steps - Order Domain

Após implementar Product, seguir a mesma metodologia para:

1. **Order** (Pedido) - Agregado principal
2. **OrderItem** - Item do pedido com produto e quantidade
3. **OrderStatus** - Status do pedido (ABERTO, AGUARDANDO_PAGAMENTO, PAGO, FINALIZADO)
4. **PaymentStatus** - Status do pagamento

## Regras de Negócio Importantes

- Pedidos começam com status ABERTO
- Só pedidos ABERTOS podem receber/alterar/remover itens
- Para finalizar: deve ter pelo menos 1 item e valor > 0
- Pagamento só em pedidos AGUARDANDO_PAGAMENTO
- Entrega só após pagamento (status PAGO)

---

**Desenvolvido com TDD e princípios SOLID** 🚀