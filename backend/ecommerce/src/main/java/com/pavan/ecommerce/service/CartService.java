package com.pavan.ecommerce.service;

import com.pavan.ecommerce.dto.CartDto;
import com.pavan.ecommerce.exception.InsufficientStockException;
import com.pavan.ecommerce.exception.ResourceNotFoundException;
import com.pavan.ecommerce.mapper.CartMapper;
import com.pavan.ecommerce.model.Cart;
import com.pavan.ecommerce.model.CartItem;
import com.pavan.ecommerce.model.Product;
import com.pavan.ecommerce.model.User;
import com.pavan.ecommerce.repositories.CartRepository;
import com.pavan.ecommerce.repositories.ProductRepository;
import com.pavan.ecommerce.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    public CartDto addToCart(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found!"));

        if(product.getQuantity() < quantity) {
            throw new InsufficientStockException("Stock not available");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElse(new Cart(null, user, new ArrayList<>()));
        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if(existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            CartItem cartItem = new CartItem(null, cart, product, quantity);
            cart.getItems().add(cartItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    public CartDto getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found!"));

        return cartMapper.toDto(cart);
    }

    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found!"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
