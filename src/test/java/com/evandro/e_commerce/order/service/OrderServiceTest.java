package com.evandro.e_commerce.order.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;
import com.evandro.e_commerce.customer.model.CustomerStatus;
import com.evandro.e_commerce.order.exception.OrderNotFoundException;
import com.evandro.e_commerce.order.model.Order;
import com.evandro.e_commerce.order.model.OrderStatus;
import com.evandro.e_commerce.order.model.PaymentStatus;
import com.evandro.e_commerce.order.repository.OrderRepository;
import com.evandro.e_commerce.product.exception.ProductNotFoundException;
import com.evandro.e_commerce.product.model.Product;
import com.evandro.e_commerce.product.service.ProductService;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;
    // @Mock
    // private CustomerRepository customerRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Customer testCustomer;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        CustomerDocuments doc = new CustomerDocuments("Test Customer", LocalDate.of(1990, 1, 1), "111.222.333-44", "1234567");
        CustomerAddress addr = new CustomerAddress("12345-678", "Test Street", 100);
        CustomerRegisterInfo info = new CustomerRegisterInfo(CustomerStatus.ACTIVE);
        testCustomer = new Customer(doc, addr, info);

        testProduct = new Product("Test Product", "Description", new BigDecimal("100.00"));

        // when(customerRepository.findById(testCustomer.getId())).thenReturn(Optional.of(testCustomer));
        when(productService.findProductById(testProduct.getId())).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
    }

    // @Test
    // @DisplayName("Should create an order successfully")
    // void shouldCreateOrderSuccessfully() {
    //     // Act
    //     Order createdOrder = orderService.createOrder(testCustomer.getId());
    //     // Assert
    //     assertNotNull(createdOrder);
    //     assertEquals(testCustomer, createdOrder.getCustomer());
    //     assertEquals(OrderStatus.OPEN, createdOrder.getStatus());
    //     assertEquals(PaymentStatus.PENDING, createdOrder.getPaymentStatus());
    //     verify(customerRepository, times(1)).findById(testCustomer.getId());
    //     verify(orderRepository, times(1)).save(any(Order.class));
    // }
    // @Test
    // @DisplayName("Should throw CustomerNotFoundException when creating order for non-existent customer")
    // void shouldThrowCustomerNotFoundExceptionWhenCreatingOrderForNonExistentCustomer() {
    //     // Arrange
    //     UUID nonExistentCustomerId = UUID.randomUUID();
    //     // when(customerRepository.findById(nonExistentCustomerId)).thenReturn(Optional.empty());
    //     // Act & Assert
    //     // assertThrows(CustomerNotFoundException.class, () -> orderService.createOrder(nonExistentCustomerId));
    //     // verify(customerRepository, times(1)).findById(nonExistentCustomerId);
    //     verify(orderRepository, never()).save(any(Order.class));
    // }
    @Test
    @DisplayName("Should find order by ID")
    void shouldFindOrderById() {
        // Arrange
        Order order = new Order(testCustomer);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // Act
        Optional<Order> foundOrder = orderService.findOrderById(order.getId());

        // Assert
        assertTrue(foundOrder.isPresent());
        assertEquals(order.getId(), foundOrder.get().getId());
        verify(orderRepository, times(1)).findById(order.getId());
    }

    @Test
    @DisplayName("Should return empty optional when order not found by ID")
    void shouldReturnEmptyOptionalWhenOrderNotFoundById() {
        // Act
        Optional<Order> foundOrder = orderService.findOrderById(UUID.randomUUID());

        // Assert
        assertFalse(foundOrder.isPresent());
        verify(orderRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should list all orders")
    void shouldListAllOrders() {
        // Arrange
        Order order1 = new Order(testCustomer);
        Order order2 = new Order(testCustomer);
        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        // Act
        List<Order> orders = orderService.listAllOrders();

        // Assert
        assertNotNull(orders);
        assertEquals(2, orders.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should list orders by customer ID")
    void shouldListOrdersByCustomerId() {
        // Arrange
        Order order1 = new Order(testCustomer);
        Order order2 = new Order(testCustomer);
        when(orderRepository.findByCustomerId(testCustomer.getId())).thenReturn(List.of(order1, order2));

        // Act
        List<Order> orders = orderService.listOrdersByCustomerId(testCustomer.getId());

        // Assert
        assertNotNull(orders);
        assertEquals(2, orders.size());
        verify(orderRepository, times(1)).findByCustomerId(testCustomer.getId());
    }

    @Test
    @DisplayName("Should add item to an OPEN order")
    void shouldAddItemToOrder() {
        // Arrange
        Order order = new Order(testCustomer);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // Act
        Order updatedOrder = orderService.addItemToOrder(order.getId(), testProduct.getId(), 2, new BigDecimal("95.00"));

        // Assert
        assertNotNull(updatedOrder);
        assertEquals(1, updatedOrder.getItems().size());
        assertEquals(2, updatedOrder.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("190.00"), updatedOrder.getTotalValue());
        verify(orderRepository, times(1)).findById(order.getId());
        verify(productService, times(1)).findProductById(testProduct.getId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when adding item to non-existent order")
    void shouldThrowOrderNotFoundExceptionWhenAddingItemToNonExistentOrder() {
        // Arrange
        UUID nonExistentOrderId = UUID.randomUUID();
        when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.addItemToOrder(nonExistentOrderId, testProduct.getId(), 1, new BigDecimal("10.00")));
        verify(orderRepository, times(1)).findById(nonExistentOrderId);
        verify(productService, never()).findProductById(any(UUID.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when adding non-existent product to order")
    void shouldThrowProductNotFoundExceptionWhenAddingNonExistentProductToOrder() {
        // Arrange
        Order order = new Order(testCustomer);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        UUID nonExistentProductId = UUID.randomUUID();
        when(productService.findProductById(nonExistentProductId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> orderService.addItemToOrder(order.getId(), nonExistentProductId, 1, new BigDecimal("10.00")));
        verify(orderRepository, times(1)).findById(order.getId());
        verify(productService, times(1)).findProductById(nonExistentProductId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should remove item from an OPEN order")
    void shouldRemoveItemFromOrder() {
        // Arrange
        Order order = new Order(testCustomer);
        order.addItem(testProduct, 1, new BigDecimal("100.00"));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // Act
        Order updatedOrder = orderService.removeItemFromOrder(order.getId(), testProduct.getId());

        // Assert
        assertNotNull(updatedOrder);
        assertTrue(updatedOrder.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, updatedOrder.getTotalValue());
        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when removing item from non-existent order")
    void shouldThrowOrderNotFoundExceptionWhenRemovingItemFromNonExistentOrder() {
        // Arrange
        UUID nonExistentOrderId = UUID.randomUUID();
        when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.removeItemFromOrder(nonExistentOrderId, testProduct.getId()));
        verify(orderRepository, times(1)).findById(nonExistentOrderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should update item quantity in an OPEN order")
    void shouldUpdateItemQuantityInOrder() {
        // Arrange
        Order order = new Order(testCustomer);
        order.addItem(testProduct, 1, new BigDecimal("100.00"));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // Act
        Order updatedOrder = orderService.updateItemQuantityInOrder(order.getId(), testProduct.getId(), 3);

        // Assert
        assertNotNull(updatedOrder);
        assertEquals(1, updatedOrder.getItems().size());
        assertEquals(3, updatedOrder.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("300.00"), updatedOrder.getTotalValue());
        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when updating item quantity in non-existent order")
    void shouldThrowOrderNotFoundExceptionWhenUpdatingItemQuantityInNonExistentOrder() {
        // Arrange
        UUID nonExistentOrderId = UUID.randomUUID();
        when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.updateItemQuantityInOrder(nonExistentOrderId, testProduct.getId(), 2));
        verify(orderRepository, times(1)).findById(nonExistentOrderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should finalize an order successfully")
    void shouldFinalizeOrderSuccessfully() {
        // Arrange
        Order order = new Order(testCustomer);
        order.addItem(testProduct, 1, new BigDecimal("100.00"));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // Act
        Order finalizedOrder = orderService.finalizeOrder(order.getId());

        // Assert
        assertNotNull(finalizedOrder);
        assertEquals(OrderStatus.WAITING_PAYMENT, finalizedOrder.getStatus());
        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when finalizing non-existent order")
    void shouldThrowOrderNotFoundExceptionWhenFinalizingNonExistentOrder() {
        // Arrange
        UUID nonExistentOrderId = UUID.randomUUID();
        when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.finalizeOrder(nonExistentOrderId));
        verify(orderRepository, times(1)).findById(nonExistentOrderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should process payment for an order successfully")
    void shouldProcessPaymentSuccessfully() {
        // Arrange
        Order order = new Order(testCustomer);
        order.addItem(testProduct, 1, new BigDecimal("100.00"));
        order.finalizeOrder();
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // Act
        Order paidOrder = orderService.processPayment(order.getId());

        // Assert
        assertNotNull(paidOrder);
        assertEquals(OrderStatus.PAID, paidOrder.getStatus());
        assertEquals(PaymentStatus.APPROVED, paidOrder.getPaymentStatus());
        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when processing payment for non-existent order")
    void shouldThrowOrderNotFoundExceptionWhenProcessingPaymentForNonExistentOrder() {
        // Arrange
        UUID nonExistentOrderId = UUID.randomUUID();
        when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.processPayment(nonExistentOrderId));
        verify(orderRepository, times(1)).findById(nonExistentOrderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should deliver an order successfully")
    void shouldDeliverOrderSuccessfully() {
        // Arrange
        Order order = new Order(testCustomer);
        order.addItem(testProduct, 1, new BigDecimal("100.00"));
        order.finalizeOrder();
        order.payOrder();
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // Act
        Order deliveredOrder = orderService.deliverOrder(order.getId());

        // Assert
        assertNotNull(deliveredOrder);
        assertEquals(OrderStatus.FINISHED, deliveredOrder.getStatus());
        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when delivering non-existent order")
    void shouldThrowOrderNotFoundExceptionWhenDeliveringNonExistentOrder() {
        // Arrange
        UUID nonExistentOrderId = UUID.randomUUID();
        when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.deliverOrder(nonExistentOrderId));
        verify(orderRepository, times(1)).findById(nonExistentOrderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should cancel an order successfully")
    void shouldCancelOrderSuccessfully() {
        // Arrange
        Order order = new Order(testCustomer);
        order.addItem(testProduct, 1, new BigDecimal("100.00"));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // Act
        Order cancelledOrder = orderService.cancelOrder(order.getId());

        // Assert
        assertNotNull(cancelledOrder);
        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());
        assertEquals(PaymentStatus.REJECTED, cancelledOrder.getPaymentStatus());
        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when cancelling non-existent order")
    void shouldThrowOrderNotFoundExceptionWhenCancellingNonExistentOrder() {
        // Arrange
        UUID nonExistentOrderId = UUID.randomUUID();
        when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(nonExistentOrderId));
        verify(orderRepository, times(1)).findById(nonExistentOrderId);
        verify(orderRepository, never()).save(any(Order.class));
    }
}
