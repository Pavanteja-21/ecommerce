package com.pavan.ecommerce.service;

import com.pavan.ecommerce.dto.CartDto;
import com.pavan.ecommerce.dto.OrderDto;
import com.pavan.ecommerce.exception.InsufficientStockException;
import com.pavan.ecommerce.exception.ResourceNotFoundException;
import com.pavan.ecommerce.mapper.CartMapper;
import com.pavan.ecommerce.mapper.OrderMapper;
import com.pavan.ecommerce.model.*;
import com.pavan.ecommerce.repositories.OrderRepository;
import com.pavan.ecommerce.repositories.ProductRepository;
import com.pavan.ecommerce.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;

    @Transactional
    public OrderDto createOrder(Long userId, String address, String phoneNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        CartDto cartDto = cartService.getCart(userId);
        Cart cart = cartMapper.toEntity(cartDto);

        if(cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create an order with empty cart");
        }

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setPhoneNumber(phoneNumber);
        order.setStatus(Order.OrderStatus.PREPARING);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = createOrderItems(cart, order);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(userId);

        try {
            emailService.sendOrderConfirmation(savedOrder);
        } catch (MailException e) {
            logger.error("Failed to send order confirmation email for order ID: " + savedOrder.getId(), e);
        }

        return orderMapper.toDto(savedOrder);
    }

    private List<OrderItem> createOrderItems(Cart cart, Order order) {
        return cart.getItems().stream().map(cartItem -> {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + cartItem.getProduct().getId()));

            if(product.getQuantity() == null) {
                throw new IllegalStateException("Product quantity is not set for product " + product.getName());
            }

            if(product.getQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException("Not enough stock for product " + product.getName());
            }

            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            return new OrderItem(null, order, product, cartItem.getQuantity(), product.getPrice());
        }).collect(Collectors.toList());
    }

    public List<OrderDto> getALlOrders() {
        return orderMapper.toDtos(orderRepository.findAll());
    }

    public List<OrderDto> getUserOrders(Long userId) {
        return orderMapper.toDtos(orderRepository.findByUserId(userId));
    }

    public OrderDto updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDto(updatedOrder);
    }


}
