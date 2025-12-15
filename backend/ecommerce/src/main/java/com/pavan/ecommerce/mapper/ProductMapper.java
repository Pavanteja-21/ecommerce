package com.pavan.ecommerce.mapper;

import com.pavan.ecommerce.dto.CommentDto;
import com.pavan.ecommerce.dto.ProductDto;
import com.pavan.ecommerce.model.Comment;
import com.pavan.ecommerce.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "image", source = "image") // add mapping
    ProductDto toDto(Product product);

    @Mapping(target = "image", source = "image") // add mapping
    Product toEntity(ProductDto productDto);

    @Mapping(target = "userId", source = "user.id")
    CommentDto toDto(Comment comment);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "product", ignore = true)
    Comment toEntity(CommentDto commentDto);
}
