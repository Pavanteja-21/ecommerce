package com.pavan.ecommerce.mapper;

import com.pavan.ecommerce.dto.CartDto;
import com.pavan.ecommerce.dto.CartItemDto;
import com.pavan.ecommerce.model.Cart;
import com.pavan.ecommerce.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "userId", source = "user.id")
    CartDto toDto(Cart cart);

    @Mapping(target = "user.id", source = "userId")
    Cart toEntity(CartDto cartDto);

    @Mapping(target = "productId", source = "product.id")
    CartItemDto toDto(CartItem cartItem);

    @Mapping(target = "product.id", source = "productId")
    CartItem toEntity(CartItemDto cartItemDto);
}
