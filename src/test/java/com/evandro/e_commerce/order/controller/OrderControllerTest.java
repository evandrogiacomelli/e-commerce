package com.evandro.e_commerce.order.controller;

 import java.math.BigDecimal;
    import java.time.LocalDate;
    import java.util.Arrays;
    import java.util.List;
    import java.util.Optional;
    import java.util.UUID;

    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.ArgumentMatchers.anyInt;
    import static org.mockito.ArgumentMatchers.eq;
    import static org.mockito.Mockito.reset;
    import static org.mockito.Mockito.times;
    import static org.mockito.Mockito.verify;
    import static org.mockito.Mockito.when;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
    import org.springframework.boot.test.mock.mockito.MockBean;
    import org.springframework.http.MediaType;
    import org.springframework.test.web.servlet.MockMvc;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

    import com.evandro.e_commerce.customer.exception.CustomerNotFoundException;
    import com.evandro.e_commerce.customer.model.Customer;
    import com.evandro.e_commerce.customer.model.CustomerAddress;
    import com.evandro.e_commerce.customer.model.CustomerDocuments;
    import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;
    import com.evandro.e_commerce.customer.model.CustomerStatus;
    import com.evandro.e_commerce.order.dto.OrderItemRequest;
    import com.evandro.e_commerce.order.dto.OrderRequest;
    import com.evandro.e_commerce.order.exception.InvalidOrderDataException; // Correct import
    import com.evandro.e_commerce.order.exception.OrderNotFoundException;
    import com.evandro.e_commerce.order.model.Order;
    import com.evandro.e_commerce.order.model.OrderItem;
    import com.evandro.e_commerce.order.model.OrderStatus;
    import com.evandro.e_commerce.order.model.PaymentStatus;
    import com.evandro.e_commerce.order.service.OrderService;
    import com.evandro.e_commerce.product.exception.ProductNotFoundException;
    import com.evandro.e_commerce.product.model.Product;
    import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(OrderController.class)
    public class OrderControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private OrderService orderService;

        private Customer testCustomer;
        private Product testProduct;
        private Order testOrder;
        private OrderItem testOrderItem;

        @BeforeEach
        void setUp() {
            CustomerDocuments doc = new CustomerDocuments("Test Customer", LocalDate.of(1990, 1, 1), "111.222.333-44", "1234567");
            CustomerAddress addr = new CustomerAddress("12345-678", "Test Street", 100);
            CustomerRegisterInfo info = new CustomerRegisterInfo(CustomerStatus.ACTIVE);
            testCustomer = new Customer(doc, addr, info);

            testProduct = new Product("Test Product", "Description", new BigDecimal("100.00"));

            testOrder = new Order(testCustomer);
            testOrderItem = new OrderItem(testProduct, 1, new BigDecimal("90.00"));
            testOrder.addItem(testProduct, 1, new BigDecimal("90.00"));

            reset(orderService);
        }

        @Test
        @DisplayName("Should create an order and return 201 CREATED")
        void shouldCreateOrder() throws Exception {
            // Arrange
            OrderRequest request = new OrderRequest(testCustomer.getId());
            when(orderService.createOrder(testCustomer.getId())).thenReturn(testOrder);
            // Act & Assert
            mockMvc.perform(post("/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.customer.id").value(testCustomer.getId().toString())) // FIX JSON PATH
                    .andExpect(jsonPath("$.status").value(OrderStatus.OPEN.toString()));
            verify(orderService, times(1)).createOrder(testCustomer.getId());
        }

        @Test
        @DisplayName("Should return 404 NOT FOUND when creating order for non-existent customer")
        void shouldReturnNotFoundWhenCreatingOrderForNonExistentCustomer() throws Exception {
            // Arrange
            UUID nonExistentCustomerId = UUID.randomUUID();
            OrderRequest request = new OrderRequest(nonExistentCustomerId);
            // FIX: Move when() before perform()
            when(orderService.createOrder(nonExistentCustomerId)).thenThrow(new CustomerNotFoundException("Customer not found"));

            // Act & Assert
            mockMvc.perform(post("/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
            verify(orderService, times(1)).createOrder(nonExistentCustomerId);
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when creating order with invalid data")
        void shouldReturnBadRequestWhenCreatingOrderWithInvalidData() throws Exception {
            // Arrange
            OrderRequest request = new OrderRequest(null); // Request with null customerId
            // FIX: Use eq(null) for precise matcher
            when(orderService.createOrder(eq(null))).thenThrow(new InvalidOrderDataException("Customer ID cannot be null"));

            // Act & Assert
            mockMvc.perform(post("/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
            // FIX: Use eq(null) in verify as well
            verify(orderService, times(1)).createOrder(eq(null));
        }

        @Test
        @DisplayName("Should get order by ID and return 200 OK")
        void shouldGetOrderById() throws Exception {
            // Arrange
            when(orderService.findOrderById(testOrder.getId())).thenReturn(Optional.of(testOrder));

            // Act & Assert
            mockMvc.perform(get("/orders/{orderId}", testOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testOrder.getId().toString()))
                    .andExpect(jsonPath("$.customer.id").value(testCustomer.getId().toString())); // FIX JSON PATH

            verify(orderService, times(1)).findOrderById(testOrder.getId());
        }

        @Test
        @DisplayName("Should return 404 NOT FOUND when order ID does not exist")
        void shouldReturnNotFoundWhenOrderIdDoesNotExist() throws Exception {
            // Arrange
            UUID nonExistentOrderId = UUID.randomUUID();
            when(orderService.findOrderById(nonExistentOrderId)).thenReturn(Optional.empty());

            // Act & Assert
            mockMvc.perform(get("/orders/{orderId}", nonExistentOrderId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(orderService, times(1)).findOrderById(nonExistentOrderId);
        }

        @Test
        @DisplayName("Should get all orders and return 200 OK")
        void shouldGetAllOrders() throws Exception {
            // Arrange
            Order order2 = new Order(testCustomer);
            order2.addItem(testProduct, 1, new BigDecimal("80.00")); // Ensure order2 has items for totalValue > 0
            List<Order> mockOrders = Arrays.asList(testOrder, order2);
            when(orderService.listAllOrders()).thenReturn(mockOrders);

            // Act & Assert
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(testOrder.getId().toString()));
                    // No customerId assertion here, as it's a list, and we're not asserting the nested customer object directly for all.
                    // If you want to assert the customer ID of the first item:
                    // .andExpect(jsonPath("$[0].customer.id").value(testCustomer.getId().toString())); // FIX JSON PATH

            verify(orderService, times(1)).listAllOrders();
        }

        @Test
        @DisplayName("Should get orders by customer ID and return 200 OK")
        void shouldGetOrdersByCustomerId() throws Exception {
            // Arrange
            Order order2 = new Order(testCustomer);
            order2.addItem(testProduct, 1, new BigDecimal("80.00")); // Ensure order2 has items for totalValue > 0
            List<Order> mockOrders = Arrays.asList(testOrder, order2);
            when(orderService.listOrdersByCustomerId(testCustomer.getId())).thenReturn(mockOrders);

            // Act & Assert
            mockMvc.perform(get("/orders/customer/{customerId}", testCustomer.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].customer.id").value(testCustomer.getId().toString())); // FIX JSON PATH

            verify(orderService, times(1)).listOrdersByCustomerId(testCustomer.getId());
        }

        @Test
        @DisplayName("Should add item to order and return 200 OK")
        void shouldAddItemToOrder() throws Exception {
            // Arrange
            OrderItemRequest request = new OrderItemRequest(testProduct.getId(), 2, new BigDecimal("95.00"));
            Order updatedOrder = new Order(testCustomer);
            updatedOrder.addItem(testProduct, 2, new BigDecimal("95.00"));
            when(orderService.addItemToOrder(eq(testOrder.getId()), eq(testProduct.getId()), eq(2), any(BigDecimal.class)))
                    .thenReturn(updatedOrder);

            // Act & Assert
            mockMvc.perform(post("/orders/{orderId}/items", testOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].product.id").value(testProduct.getId().toString()))
                    .andExpect(jsonPath("$.items[0].quantity").value(2));

            verify(orderService, times(1)).addItemToOrder(eq(testOrder.getId()), eq(testProduct.getId()), eq(2), any(BigDecimal.class));
        }

        @Test
        @DisplayName("Should return 404 NOT FOUND when adding item to non-existent order")
        void shouldReturnNotFoundWhenAddingItemToNonExistentOrder() throws Exception {
            // Arrange
            UUID nonExistentOrderId = UUID.randomUUID();
            OrderItemRequest request = new OrderItemRequest(testProduct.getId(), 1, new BigDecimal("10.00"));
            when(orderService.addItemToOrder(eq(nonExistentOrderId), any(UUID.class), anyInt(), any(BigDecimal.class)))
                    .thenThrow(new OrderNotFoundException("Order not found"));

            // Act & Assert
            mockMvc.perform(post("/orders/{orderId}/items", nonExistentOrderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());

            verify(orderService, times(1)).addItemToOrder(eq(nonExistentOrderId), any(UUID.class), anyInt(), any(BigDecimal.class));
        }

        @Test
        @DisplayName("Should return 404 NOT FOUND when adding non-existent product to order")
        void shouldReturnNotFoundWhenAddingNonExistentProductToOrder() throws Exception {
            // Arrange
            UUID nonExistentProductId = UUID.randomUUID();
            OrderItemRequest request = new OrderItemRequest(nonExistentProductId, 1, new BigDecimal("10.00"));
            when(orderService.addItemToOrder(eq(testOrder.getId()), eq(nonExistentProductId), anyInt(), any(BigDecimal.class)))
                    .thenThrow(new ProductNotFoundException("Product not found"));

            // Act & Assert
            mockMvc.perform(post("/orders/{orderId}/items", testOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());

            verify(orderService, times(1)).addItemToOrder(eq(testOrder.getId()), eq(nonExistentProductId), anyInt(), any(BigDecimal.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when adding item with invalid quantity or to non-OPEN order")
        void shouldReturnBadRequestWhenAddingItemWithInvalidData() throws Exception {
            // Arrange
            OrderItemRequest request = new OrderItemRequest(testProduct.getId(), 0, new BigDecimal("10.00"));
            when(orderService.addItemToOrder(eq(testOrder.getId()), any(UUID.class), eq(0), any(BigDecimal.class))) // FIX: Use eq(0)
                    .thenThrow(new IllegalArgumentException("Quantity must be greater than zero."));

            // Act & Assert
            mockMvc.perform(post("/orders/{orderId}/items", testOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(orderService, times(1)).addItemToOrder(eq(testOrder.getId()), any(UUID.class), eq(0), any(BigDecimal.class)); // FIX: Use eq(0)
        }

        @Test
        @DisplayName("Should remove item from order and return 200 OK")
        void shouldRemoveItemFromOrder() throws Exception {
            // Arrange
            Order orderWithItemRemoved = new Order(testCustomer);
            when(orderService.removeItemFromOrder(eq(testOrder.getId()), eq(testProduct.getId())))
                    .thenReturn(orderWithItemRemoved);

            // Act & Assert
            mockMvc.perform(delete("/orders/{orderId}/items/{productId}", testOrder.getId(), testProduct.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(0));

            verify(orderService, times(1)).removeItemFromOrder(eq(testOrder.getId()), eq(testProduct.getId()));
        }

        @Test
        @DisplayName("Should return 404 NOT FOUND when removing item from non-existent order")
        void shouldReturnNotFoundWhenRemovingItemFromNonExistentOrder() throws Exception {
            // Arrange
            UUID nonExistentOrderId = UUID.randomUUID();
            when(orderService.removeItemFromOrder(eq(nonExistentOrderId), any(UUID.class)))
                    .thenThrow(new OrderNotFoundException("Order not found"));

            // Act & Assert
            mockMvc.perform(delete("/orders/{orderId}/items/{productId}", nonExistentOrderId, testProduct.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(orderService, times(1)).removeItemFromOrder(eq(nonExistentOrderId), any(UUID.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when removing non-existent item or from non-OPEN order")
        void shouldReturnBadRequestWhenRemovingInvalidItem() throws Exception {
            // Arrange
            UUID nonExistentProductIdInOrder = UUID.randomUUID(); // Use a specific UUID
            when(orderService.removeItemFromOrder(eq(testOrder.getId()), eq(nonExistentProductIdInOrder))) // FIX: Use specific UUID
                    .thenThrow(new IllegalArgumentException("Product not found in order"));

            // Act & Assert
            mockMvc.perform(delete("/orders/{orderId}/items/{productId}", testOrder.getId(), nonExistentProductIdInOrder) // FIX: Use specific UUID
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(orderService, times(1)).removeItemFromOrder(eq(testOrder.getId()), eq(nonExistentProductIdInOrder)); // FIX: Use specific UUID
        }

        @Test
        @DisplayName("Should update item quantity in order and return 200 OK")
        void shouldUpdateItemQuantityInOrder() throws Exception {
            // Arrange
            OrderItemRequest request = new OrderItemRequest(testProduct.getId(), 3, new BigDecimal("90.00"));
            Order updatedOrder = new Order(testCustomer);
            updatedOrder.addItem(testProduct, 3, new BigDecimal("90.00"));
            when(orderService.updateItemQuantityInOrder(eq(testOrder.getId()), eq(testProduct.getId()), eq(3)))
                    .thenReturn(updatedOrder);

            // Act & Assert
            mockMvc.perform(put("/orders/{orderId}/items/{productId}", testOrder.getId(), testProduct.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].quantity").value(3));

            verify(orderService, times(1)).updateItemQuantityInOrder(eq(testOrder.getId()), eq(testProduct.getId()), eq(3));
        }

        @Test
        @DisplayName("Should return 404 NOT FOUND when updating item quantity in non-existent order")
        void shouldReturnNotFoundWhenUpdatingItemQuantityInNonExistentOrder() throws Exception {
            // Arrange
            UUID nonExistentOrderId = UUID.randomUUID();
            OrderItemRequest request = new OrderItemRequest(testProduct.getId(), 2, new BigDecimal("10.00"));
            when(orderService.updateItemQuantityInOrder(eq(nonExistentOrderId), any(UUID.class), anyInt()))
                    .thenThrow(new OrderNotFoundException("Order not found"));

            // Act & Assert
            mockMvc.perform(put("/orders/{orderId}/items/{productId}", nonExistentOrderId, testProduct.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());

            verify(orderService, times(1)).updateItemQuantityInOrder(eq(nonExistentOrderId), any(UUID.class), anyInt());
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when updating item quantity with invalid data or to non-OPEN order")
        void shouldReturnBadRequestWhenUpdatingItemQuantityWithInvalidData() throws Exception {
            // Arrange
            OrderItemRequest request = new OrderItemRequest(testProduct.getId(), -1, new BigDecimal("10.00"));
            when(orderService.updateItemQuantityInOrder(eq(testOrder.getId()), any(UUID.class), eq(-1))) // FIX: Use eq(-1)
                    .thenThrow(new IllegalArgumentException("Quantity must be greater than zero."));

            // Act & Assert
            mockMvc.perform(put("/orders/{orderId}/items/{productId}", testOrder.getId(), testProduct.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(orderService, times(1)).updateItemQuantityInOrder(eq(testOrder.getId()), any(UUID.class), eq(-1)); // FIX: Use eq(-1)
        }

        @Test
        @DisplayName("Should finalize an order and return 200 OK")
        void shouldFinalizeOrder() throws Exception {
            // Arrange
            Order finalizedOrder = new Order(testCustomer);
            finalizedOrder.addItem(testProduct, 1, new BigDecimal("90.00"));
            finalizedOrder.finalizeOrder();
            when(orderService.finalizeOrder(testOrder.getId())).thenReturn(finalizedOrder);

            // Act & Assert
            mockMvc.perform(patch("/orders/{orderId}/finalize", testOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(OrderStatus.WAITING_PAYMENT.toString()));

            verify(orderService, times(1)).finalizeOrder(testOrder.getId());
        }

        @Test
        @DisplayName("Should return 404 NOT FOUND when finalizing non-existent order")
        void shouldReturnNotFoundWhenFinalizingNonExistentOrder() throws Exception {
            // Arrange
            UUID nonExistentOrderId = UUID.randomUUID();
            when(orderService.finalizeOrder(nonExistentOrderId)).thenThrow(new OrderNotFoundException("Order not found"));

            // Act & Assert
            mockMvc.perform(patch("/orders/{orderId}/finalize", nonExistentOrderId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(orderService, times(1)).finalizeOrder(nonExistentOrderId);
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when finalizing empty order or non-OPEN order")
        void shouldReturnBadRequestWhenFinalizingInvalidOrder() throws Exception {
            // Arrange
            when(orderService.finalizeOrder(testOrder.getId())).thenThrow(new IllegalStateException("Order must have at least one item."));

            // Act & Assert
            mockMvc.perform(patch("/orders/{orderId}/finalize", testOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(orderService, times(1)).finalizeOrder(testOrder.getId());
        }

        @Test
        @DisplayName("Should process payment for an order and return 200 OK")
        void shouldProcessPayment() throws Exception {
            // Arrange
            Order paidOrder = new Order(testCustomer);
            paidOrder.addItem(testProduct, 1, new BigDecimal("90.00"));
            paidOrder.finalizeOrder();
            paidOrder.payOrder();
            when(orderService.processPayment(testOrder.getId())).thenReturn(paidOrder);

            // Act & Assert
            mockMvc.perform(patch("/orders/{orderId}/pay", testOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(OrderStatus.PAID.toString()))
                    .andExpect(jsonPath("$.paymentStatus").value(PaymentStatus.APPROVED.toString()));

            verify(orderService, times(1)).processPayment(testOrder.getId());
        }

        @Test
        @DisplayName("Should return 404 NOT FOUND when processing payment for non-existent order")
        void shouldReturnNotFoundWhenProcessingPaymentForNonExistentOrder() throws Exception {
            // Arrange
            UUID nonExistentOrderId = UUID.randomUUID();
            when(orderService.processPayment(nonExistentOrderId)).thenThrow(new OrderNotFoundException("Order not found"));

            // Act & Assert
            mockMvc.perform(patch("/orders/{orderId}/pay", nonExistentOrderId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(orderService, times(1)).processPayment(nonExistentOrderId);
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when processing payment for non-WAITING_PAYMENT order")
        void shouldReturnBadRequestWhenProcessingPaymentForInvalidStatusOrder() throws Exception {
            // Arrange
            when(orderService.processPayment(testOrder.getId())).thenThrow(new IllegalStateException("Order is not in WAITING_PAYMENT status."));

            // Act & Assert
            mockMvc.perform(patch("/orders/{orderId}/pay", testOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(orderService, times(1)).processPayment(testOrder.getId());
        }

        @Test
        @DisplayName("Should deliver an order and return 200 OK")
        void shouldDeliverOrder() throws Exception {
            // Arrange
            Order deliveredOrder = new Order(testCustomer);
            deliveredOrder.addItem(testProduct, 1, new BigDecimal("90.00"));
            deliveredOrder.finalizeOrder();
            deliveredOrder.payOrder();
            deliveredOrder.deliverOrder();
            when(orderService.deliverOrder(testOrder.getId())).thenReturn(deliveredOrder);

            // Act & Assert
            mockMvc.perform(patch("/orders/{orderId}/deliver", testOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(OrderStatus.FINISHED.toString()));

            verify(orderService, times(1)).deliverOrder(testOrder.getId());
        }

        @Test
        @DisplayName("Should return 404 NOT FOUND when delivering non-existent order")
        void shouldReturnNotFoundWhenDeliveringNonExistentOrder() throws Exception {
            // Arrange
            UUID nonExistentOrderId = UUID.randomUUID();
            when(orderService.deliverOrder(nonExistentOrderId)).thenThrow(new OrderNotFoundException("Order not found"));

            // Act & Assert
            mockMvc.perform(patch("/orders/{orderId}/deliver", nonExistentOrderId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(orderService, times(1)).deliverOrder(nonExistentOrderId);
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when delivering non-PAID order")
        void shouldReturnBadRequestWhenDeliveringInvalidStatusOrder() throws Exception {
            // Arrange
            when(orderService.deliverOrder(testOrder.getId())).thenThrow(new IllegalStateException("Order is not in PAID status."));

            // Act & Assert
            mockMvc.perform(patch("/orders/{orderId}/deliver", testOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(orderService, times(1)).deliverOrder(testOrder.getId());
        }

        @Test
        @DisplayName("Should cancel an order and return 200 OK")
        void shouldCancelOrder() throws Exception {
            // Arrange
            Order cancelledOrder = new Order(testCustomer);
            cancelledOrder.addItem(testProduct, 1, new BigDecimal("90.00"));
            cancelledOrder.cancelOrder();
            when(orderService.cancelOrder(testOrder.getId())).thenReturn(cancelledOrder);

            // Act & Assert
            mockMvc.perform(patch("/orders/{orderId}/cancel", testOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(OrderStatus.CANCELLED.toString()))
                    .andExpect(jsonPath("$.paymentStatus").value(PaymentStatus.REJECTED.toString()));

            verify(orderService, times(1)).cancelOrder(testOrder.getId());
        }

        @Test
        @DisplayName("Should return 404 NOT FOUND when cancelling non-existent order")
        void shouldReturnNotFoundWhenCancellingNonExistentOrder() throws Exception {
            // Arrange
            UUID nonExistentOrderId = UUID.randomUUID();
            when(orderService.cancelOrder(nonExistentOrderId)).thenThrow(new OrderNotFoundException("Order not found"));

            // Act & Assert
            mockMvc.perform(patch("/orders/{orderId}/cancel", nonExistentOrderId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(orderService, times(1)).cancelOrder(nonExistentOrderId);
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when cancelling FINISHED or PAID order")
        void shouldReturnBadRequestWhenCancellingInvalidStatusOrder() throws Exception {
            // Arrange
            when(orderService.cancelOrder(testOrder.getId())).thenThrow(new IllegalStateException("Cannot cancel an order that is already FINISHED or PAID."));

            // Act & Assert
            mockMvc.perform(patch("/orders/{orderId}/cancel", testOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(orderService, times(1)).cancelOrder(testOrder.getId());
        }
}
