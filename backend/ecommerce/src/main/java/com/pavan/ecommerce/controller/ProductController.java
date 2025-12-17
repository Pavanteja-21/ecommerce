package com.pavan.ecommerce.controller;

import com.pavan.ecommerce.dto.ProductDto;
import com.pavan.ecommerce.dto.ProductListDto;
import com.pavan.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> createProduct(@RequestPart("product") @Valid ProductDto productDto,
                                                    @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(productService.createProduct(productDto, image));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id,
                                                    @RequestPart("product") @Valid ProductDto productDto,
                                                    @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(productService.updateProduct(id, productDto, image));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductListDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
}
