package com.pavan.ecommerce.service;

import com.pavan.ecommerce.dto.CommentDto;
import com.pavan.ecommerce.exception.ResourceNotFoundException;
import com.pavan.ecommerce.mapper.CommentMapper;
import com.pavan.ecommerce.model.Comment;
import com.pavan.ecommerce.model.Product;
import com.pavan.ecommerce.model.User;
import com.pavan.ecommerce.repositories.CommentRepository;
import com.pavan.ecommerce.repositories.ProductRepository;
import com.pavan.ecommerce.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public CommentDto addComment(Long productId, Long userId, CommentDto commentDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setProduct(product);
        comment.setUser(user);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    public List<CommentDto> getCommentsByProduct(Long productId) {
        List<Comment> comments = commentRepository.findByProductId(productId);
        return comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }
}
