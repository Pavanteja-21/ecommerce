package com.pavan.ecommerce.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDto {
    private Long id;

    private Long userId; // userId

    private List<CartItemDto> items;
}
