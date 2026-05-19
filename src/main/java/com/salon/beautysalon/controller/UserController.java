package com.salon.beautysalon.controller;

import com.salon.beautysalon.dto.request.AuthRequest;
import com.salon.beautysalon.dto.request.UserRegisterRequest;
import com.salon.beautysalon.dto.request.UserUpdateRequest;
import com.salon.beautysalon.dto.response.AuthResponse;
import com.salon.beautysalon.dto.response.UserResponse;
import com.salon.beautysalon.model.User;
import com.salon.beautysalon.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest request) {
        try {
            User saved = service.register(toUser(request));
            return ResponseEntity.status(HttpStatus.CREATED).body(toAuthResponse(saved));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        User user = service.login(request.email(), request.password());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(error("Invalid email or password"));
        }

        return ResponseEntity.ok(toAuthResponse(user));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAll(@RequestParam Long adminId) {
        try {
            service.requireAdmin(adminId);
            List<UserResponse> users = service.getAllUsers().stream().map(this::toUserResponse).toList();
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(ex.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestParam Long adminId) {
        try {
            service.deleteUserAsAdmin(adminId, id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(ex.getMessage()));
        }
    }

    @GetMapping("/users/me")
    public ResponseEntity<?> getMe(@RequestParam Long userId) {
        try {
            service.requireUser(userId);
            return ResponseEntity.ok(toUserResponse(service.getById(userId)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(ex.getMessage()));
        }
    }

    @PutMapping("/users/me")
    public ResponseEntity<?> updateMe(@RequestParam Long userId, @RequestBody UserUpdateRequest request) {
        try {
            User updated = service.updateProfile(userId, toUser(request));
            return ResponseEntity.ok(toUserResponse(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(ex.getMessage()));
        }
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getContactNo(), user.getRole(), user.getProfileImage());
    }

    private User toUser(UserRegisterRequest request) {
        User user = new User();
        if (request != null) {
            user.setName(request.name());
            user.setEmail(request.email());
            user.setContactNo(request.contactNo());
            user.setPassword(request.password());
            user.setProfileImage(request.profileImage());
        }
        return user;
    }

    private User toUser(UserUpdateRequest request) {
        User user = new User();
        if (request != null) {
            user.setName(request.name());
            user.setEmail(request.email());
            user.setContactNo(request.contactNo());
            user.setPassword(request.password());
            user.setProfileImage(request.profileImage());
        }
        return user;
    }

    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getContactNo(), user.getRole(), user.getProfileImage());
    }

    private Map<String, String> error(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("message", message);
        return body;
    }
}