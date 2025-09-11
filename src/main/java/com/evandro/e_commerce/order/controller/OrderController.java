package com.evandro.e_commerce.order.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evandro.e_commerce.order.dto.OrderItemRequest;
import com.evandro.e_commerce.order.dto.OrderResponse;
import com.evandro.e_commerce.order.exception.OrderNotFoundException;
import com.evandro.e_commerce.order.model.Order;
import com.evandro.e_commerce.order.service.OrderService;
import com.evandro.e_commerce.product.exception.ProductNotFoundException;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // @PostMapping
    // public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
    //     try {
    //         Order order = orderService.createOrder(request.getCustomerId());
    //         return ResponseEntity.status(HttpStatus.CREATED).body(new OrderResponse(order));
    //     } catch (CustomerNotFoundException e) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    //     } catch (InvalidOrderDataException e) {
    //         return ResponseEntity.badRequest().build();
    //     }
    // }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID orderId) {
        return orderService.findOrderById(orderId)
                .map(OrderResponse::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.listAllOrders().stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId(@PathVariable UUID customerId) {
        List<OrderResponse> orders = orderService.listOrdersByCustomerId(customerId).stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderResponse> addItemToOrder(@PathVariable UUID orderId, @RequestBody OrderItemRequest request) {
        try {
            Order updatedOrder = orderService.addItemToOrder(orderId, request.getProductId(), request.getQuantity(), request.getSalePrice());
            return ResponseEntity.ok(new OrderResponse(updatedOrder));
        } catch (OrderNotFoundException | ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{orderId}/items/{productId}")
    public ResponseEntity<OrderResponse> removeItemFromOrder(@PathVariable UUID orderId, @PathVariable UUID productId) {
        try {
            Order updatedOrder = orderService.removeItemFromOrder(orderId, productId);
            return ResponseEntity.ok(new OrderResponse(updatedOrder));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{orderId}/items/{productId}")
    public ResponseEntity<OrderResponse> updateItemQuantityInOrder(@PathVariable UUID orderId, @PathVariable UUID productId, @RequestBody OrderItemRequest request) {
        try {
            Order updatedOrder = orderService.updateItemQuantityInOrder(orderId, productId, request.getQuantity());
            return ResponseEntity.ok(new OrderResponse(updatedOrder));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{orderId}/finalize")
    public ResponseEntity<OrderResponse> finalizeOrder(@PathVariable UUID orderId) {
        try {
            Order finalizedOrder = orderService.finalizeOrder(orderId);
            return ResponseEntity.ok(new OrderResponse(finalizedOrder));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{orderId}/pay")
    public ResponseEntity<OrderResponse> processPayment(@PathVariable UUID orderId) {
        try {
            Order paidOrder = orderService.processPayment(orderId);
            return ResponseEntity.ok(new OrderResponse(paidOrder));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{orderId}/deliver")
    public ResponseEntity<OrderResponse> deliverOrder(@PathVariable UUID orderId) {
        try {
            Order deliveredOrder = orderService.deliverOrder(orderId);
            return ResponseEntity.ok(new OrderResponse(deliveredOrder));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID orderId) {
        try {
            Order cancelledOrder = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(new OrderResponse(cancelledOrder));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
