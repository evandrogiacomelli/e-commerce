package com.evandro.e_commerce.notification.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        } catch (Exception e) {
            logger.error("Failed to send email to: {} - Order: {}",
                        customer.getDocuments().getEmail(), order.getId(), e);
            throw new RuntimeException("Failed to send order update email", e);
        }
    }

    private String buildHtmlEmailContent(Customer customer, Order order) {
        String statusColor = getStatusColor(order.getStatus());
        String statusIcon = getStatusIcon(order.getStatus());
        String paymentStatusIcon = getPaymentStatusIcon(order.getPaymentStatus());
        String paymentStatusColor = getPaymentStatusColor(order.getPaymentStatus());

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        margin: 0;
                        padding: 0;
                        background-color: #f4f4f4;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    .header {
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                        padding: 25px;
                        text-align: center;
                        border-radius: 10px 10px 0 0;
                        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                    }
                    .content {
                        background-color: white;
                        padding: 30px;
                        border-radius: 0 0 10px 10px;
                        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                    }
                    .order-info {
                        background: linear-gradient(135deg, #f5f7fa 0%%, #c3cfe2 100%%);
                        padding: 20px;
                        border-radius: 8px;
                        margin: 20px 0;
                        border-left: 5px solid #667eea;
                    }
                    .status {
                        font-weight: bold;
                        color: %s;
                        font-size: 18px;
                    }
                    .payment-status {
                        font-weight: bold;
                        color: %s;
                        font-size: 16px;
                    }
                    .footer {
                        text-align: center;
                        color: #666;
                        font-size: 12px;
                        margin-top: 20px;
                        padding: 20px;
                        border-top: 2px solid #eee;
                    }
                    .items-list {
                        list-style: none;
                        padding: 0;
                        margin: 15px 0;
                    }
                    .items-list li {
                        background-color: #f8f9fa;
                        margin: 8px 0;
                        padding: 15px;
                        border-radius: 5px;
                        border-left: 4px solid #28a745;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.05);
                    }
                    .highlight {
                        background: linear-gradient(135deg, #e8f5e8 0%%, #d4e6d4 100%%);
                        padding: 15px;
                        border-radius: 8px;
                        margin: 15px 0;
                        border-left: 4px solid #28a745;
                    }
                    .total-value {
                        font-size: 24px;
                        font-weight: bold;
                        color: #28a745;
                        text-align: center;
                        background-color: #f8f9fa;
                        padding: 15px;
                        border-radius: 8px;
                        margin: 20px 0;
                    }
                    .order-details {
                        display: grid;
                        grid-template-columns: 1fr 1fr;
                        gap: 15px;
                        margin: 20px 0;
                    }
                    .detail-box {
                        background-color: #f8f9fa;
                        padding: 15px;
                        border-radius: 5px;
                        text-align: center;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🛍️ E-Commerce Store</h1>
                        <p>Atualização do seu Pedido</p>
                        <p style="font-size: 14px; opacity: 0.9;">Sistema integrado com MailerSend SMTP</p>
                    </div>

                    <div class="content">
                        <h2>Olá, %s! 👋</h2>
                        <p>Temos uma atualização importante sobre seu pedido:</p>

                        <div class="order-info">
                            <h3>📦 Pedido #%s</h3>

                            <div class="order-details">
                                <div class="detail-box">
                                    <strong>📅 Criado em:</strong><br>
                                    %s
                                </div>
                                <div class="detail-box">
                                    <strong>🔄 Última atualização:</strong><br>
                                    %s
                                </div>
                            </div>

                            <div class="highlight" style="margin: 20px 0;">
                                <p><strong>Status atual:</strong> <span class="status">%s %s</span></p>
                            </div>

                            <div style="margin: 20px 0;">
                                <p><strong>Status do Pagamento:</strong> <span class="payment-status">%s %s</span></p>
                            </div>

                            <div class="total-value">
                                💰 Valor Total: R$ %.2f
                            </div>
                        </div>

                        <h4>🛒 Itens do Pedido:</h4>
                        <ul class="items-list">
                            %s
                        </ul>

                        <div class="highlight">
                            <p><strong>%s</strong></p>
                        </div>

                        <div style="text-align: center; margin: 30px 0;">
                            <p style="font-size: 18px;">Obrigado pela preferência! 😊</p>
                            <p style="color: #666;">Acompanhe seu pedido através do nosso sistema.</p>
                        </div>
                    </div>

                    <div class="footer">
                        <p>Este é um email automático, não responda.</p>
                        <p><strong>© 2024 E-Commerce Store</strong></p>
                        <p>Sistema desenvolvido com Spring Boot + JPA + MailerSend SMTP</p>
                        <p>Projeto acadêmico - Demonstração de tecnologias enterprise</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                statusColor,
                paymentStatusColor,
                customer.getDocuments().getName(),
                order.getId(),
                order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                statusIcon,
                order.getStatus(),
                paymentStatusIcon,
                order.getPaymentStatus(),
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

    private String getStatusIcon(OrderStatus status) {
        return switch (status) {
            case OPEN -> "📝";
            case WAITING_PAYMENT -> "⏳";
            case PAID -> "✅";
            case FINISHED -> "📦";
            case CANCELLED -> "❌";
        };
    }

    private String getPaymentStatusColor(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case PENDING -> "#ffc107";
            case APPROVED -> "#28a745";
            case REJECTED -> "#dc3545";
            case REFUNDED -> "#6c757d";
        };
    }

    private String getPaymentStatusIcon(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case PENDING -> "⏳";
            case APPROVED -> "✅";
            case REJECTED -> "❌";
            case REFUNDED -> "↩️";
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
                <li>
                    <strong>🎯 %s</strong><br>
                    <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; margin-top: 10px;">
                        <div>Quantidade: <strong>%d</strong></div>
                        <div>Preço unit: <strong>R$ %.2f</strong></div>
                        <div>Subtotal: <strong style="color: #28a745;">R$ %.2f</strong></div>
                    </div>
                </li>
                """.formatted(
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getSalePrice(),
                    item.getSubtotal()
                ))
            .collect(Collectors.joining());
    }
}