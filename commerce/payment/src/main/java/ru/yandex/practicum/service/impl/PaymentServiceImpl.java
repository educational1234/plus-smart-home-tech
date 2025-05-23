package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.exception.NoPaymentFoundException;
import ru.yandex.practicum.feign.OrderClient;
import ru.yandex.practicum.feign.ShoppingStoreClient;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.model.PaymentStatus;
import ru.yandex.practicum.repository.PaymentRepository;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderClient orderClient;
    private final ShoppingStoreClient shoppingStoreClient;



    @Override
    public PaymentDto createPayment(PaymentDto dto) {
        OrderDto order = orderClient.getOrderById(dto.getOrderId());

        double productPrice = order.getProducts().entrySet().stream()
                .mapToDouble(entry -> {
                    UUID productId = entry.getKey();
                    long quantity = entry.getValue();
                    return shoppingStoreClient.getProductById(productId).getPrice() * quantity;
                })
                .sum();

        double deliveryPrice = safe(dto.getDeliveryPrice());
        double vat = productPrice * 0.10;
        double total = productPrice + vat + deliveryPrice;

        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID())
                .orderId(dto.getOrderId())
                .amount(productPrice)
                .deliveryPrice(deliveryPrice)
                .totalPrice(total)
                .status(PaymentStatus.valueOf(PaymentStatus.PENDING.name()))
                .build();

        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public Double calculateProductCost(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoPaymentFoundException("Платеж не найден для заказа: " + orderId));
        return safe(payment.getAmount());
    }

    @Override
    public Double calculateTotalCost(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoPaymentFoundException("Платеж не найден для заказа: " + orderId));
        return safe(payment.getTotalPrice());
    }

    @Override
    public PaymentDto paymentSuccess(UUID paymentId) {
        Payment payment = findById(paymentId);
        payment.setStatus(PaymentStatus.valueOf(PaymentStatus.SUCCESS.name()));
        orderClient.paymentSuccess(payment.getOrderId());
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public PaymentDto paymentFailed(UUID paymentId) {
        Payment payment = findById(paymentId);
        payment.setStatus(PaymentStatus.valueOf(PaymentStatus.FAILED.name()));
        orderClient.paymentFailed(payment.getOrderId());
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    private Payment findById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new NoPaymentFoundException("Платеж не найден(id): " + id));
    }

    private double safe(Double val) {
        return val != null ? val : 0.0;
    }
}
