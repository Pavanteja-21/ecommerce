package com.pavan.ecommerce.mapper;

import com.pavan.ecommerce.dto.OrderDto;
import com.pavan.ecommerce.dto.OrderItemDto;
import com.pavan.ecommerce.model.Order;
import com.pavan.ecommerce.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "orderItems", source = "items")
    OrderDto toDto(Order order);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "items", source = "orderItems")
    Order toEntity(OrderDto orderDto);

    List<OrderDto> toDtos(List<Order> orders);

    List<Order> toEntities(List<OrderDto> orderDtos);

    @Mapping(target = "productId", source = "product.id")
    OrderItemDto toOrderItemDto(OrderItem orderItem);

    @Mapping(target = "product.id", source = "productId")
    OrderItem toOrderItemEntity(OrderItemDto orderItemDto);

    List<OrderItemDto> toOrderItemDtos(List<OrderItem> orderItem);

    List<OrderItem> toOrderItemEntities(List<OrderItemDto> orderItemDto);

}
