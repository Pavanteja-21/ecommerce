package com.pavan.ecommerce.controller;

import com.pavan.ecommerce.dto.ChangePasswordRequest;
import com.pavan.ecommerce.dto.EmailConfirmationRequest;
import com.pavan.ecommerce.dto.LoginRequest;
import com.pavan.ecommerce.exception.ResourceNotFoundException;
import com.pavan.ecommerce.model.User;
import com.pavan.ecommerce.service.CartService;
import com.pavan.ecommerce.service.JwtService;
import com.pavan.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final CartService cartService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        final UserDetails userDetails = userService.getUserByEmail(loginRequest.getEmail());
        final String jwt = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        cartService.createCartForUser(registeredUser.getId());
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        userService.changePassword(email, request);
        return ResponseEntity.ok().body("Password Changed");
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(@RequestBody EmailConfirmationRequest request) {
        try {
            userService.confirmEmail(request.getEmail(), request.getConfirmationCode());
            return ResponseEntity.ok().body("Email confirmed successfully");
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Invalid confirmation code");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/role")
    public ResponseEntity<String> getUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        if (user != null) {
            String role = String.valueOf(user.getRole());
            return ResponseEntity.ok(role);
        }

        return ResponseEntity.notFound().build();
    }


    @GetMapping("/user/confirmation")
    public ResponseEntity<String> getUserConfirmation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        if (user != null) {
            String confirmed = String.valueOf(user.getEmailConfirmation());
            return ResponseEntity.ok(confirmed);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<String> getUserEmailById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user.getEmail());
        }

        return ResponseEntity.notFound().build();
    }


}
