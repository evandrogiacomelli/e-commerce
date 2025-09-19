package com.evandro.e_commerce.notification.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.order.model.Order;
import com.evandro.e_commerce.order.model.OrderItem;
import com.evandro.e_commerce.order.model.OrderStatus;
import com.evandro.e_commerce.order.model.PaymentStatus;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${email.from.address}")
    private String fromAddress;

    @Value("${email.from.name}")
    private String fromName;

    @Override
    public void sendOrderUpdateEmail(Customer customer, Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress, fromName);
            helper.setTo(customer.getDocuments().getEmail());
            helper.setSubject("🛍️ Pedido " + order.getId() + " - Status Atualizado");
            helper.setText(buildHtmlEmailContent(customer, order), true);

            mailSender.send(message);

            logger.info("Email sent successfully to: {} - Order: {} - Status: {}",
                       customer.getDocuments().getEmail(), order.getId(), order.getStatus());

        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("Failed to send email to: {} - Order: {}",
                        customer.getDocuments().getEmail(), order.getId(), e);
            throw new RuntimeException("Failed to send order update email", e);
        }
    }

    private String buildHtmlEmailContent(Customer customer, Order order) {
        String statusColor = getStatusColor(order.getStatus());

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        margin: 0;
                        padding: 20px;
                        background-color: #f8f9fa;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        background-color: white;
                        padding: 20px;
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 30px;
                    }
                    .status-badge {
                        background-color: %s;
                        color: white;
                        padding: 5px 10px;
                        border-radius: 5px;
                        font-size: 14px;
                    }
                    .total {
                        font-size: 18px;
                        font-weight: bold;
                        margin: 15px 0;
                    }
                    .item {
                        padding: 5px 0;
                        display: flex;
                        justify-content: space-between;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>E-Commerce Store</h1>
                        <p>Atualização do seu pedido</p>
                    </div>

                    <h2>Olá, %s!</h2>
                    <p>Seu pedido foi atualizado:</p>

                    <h3>Pedido #%s</h3>
                    <span class="status-badge">%s</span>

                    <div class="total">
                        Total: R$ %.2f
                    </div>

                    %s

                    <p>%s</p>

                    <p>Obrigado por escolher nossa loja!</p>
                </div>
            </body>
            </html>
            """.formatted(
                statusColor,
                customer.getDocuments().getName(),
                order.getId(),
                order.getStatus(),
                order.getTotalValue(),
                buildItemsHtml(order.getItems()),
                getStatusMessage(order.getStatus(), order.getPaymentStatus())
            );
    }

    private String getStatusColor(OrderStatus status) {
        return switch (status) {
            case OPEN -> "#17a2b8";
            case WAITING_PAYMENT -> "#ffc107";
            case PAID -> "#28a745";
            case FINISHED -> "#6f42c1";
            case CANCELLED -> "#dc3545";
        };
    }


    private String getStatusMessage(OrderStatus status, PaymentStatus paymentStatus) {
        return switch (status) {
            case OPEN -> "Seu pedido está aberto. Continue adicionando itens ou finalize-o para prosseguir.";
            case WAITING_PAYMENT -> "Seu pedido está aguardando pagamento. Efetue o pagamento para continuar.";
            case PAID -> "Pagamento aprovado! Seu pedido está sendo preparado para envio.";
            case FINISHED -> "Seu pedido foi concluído com sucesso! Obrigado pela compra.";
            case CANCELLED -> "Seu pedido foi cancelado." +
                (paymentStatus == PaymentStatus.REFUNDED ? " O reembolso foi processado." : "");
        };
    }

    private String buildItemsHtml(List<OrderItem> items) {
        return items.stream()
            .map(item -> """
                <div class="item">
                    <span>%s (x%d)</span>
                    <span>R$ %.2f</span>
                </div>
                """.formatted(
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getSubtotal()
                ))
            .collect(Collectors.joining());
    }
}