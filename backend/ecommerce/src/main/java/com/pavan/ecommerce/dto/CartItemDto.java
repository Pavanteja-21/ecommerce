package com.pavan.ecommerce.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemDto {
    private Long id;

    private Long productId;

    @Positive
    private Integer quantity;
}
