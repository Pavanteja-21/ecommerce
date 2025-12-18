package com.pavan.ecommerce.repositories;

import com.pavan.ecommerce.dto.ProductListDto;
import com.pavan.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT new com.pavan.ecommerce.dto.ProductListDto(p.id, p.name, p.description, p.price, p.quantity, p.image) FROM Product p")
    Page<ProductListDto> findAllWithoutComments(Pageable pageable);
}
