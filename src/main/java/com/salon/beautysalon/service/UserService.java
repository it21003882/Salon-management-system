package com.salon.beautysalon.service;

import com.salon.beautysalon.model.User;
import com.salon.beautysalon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {

    private static final int MAX_PROFILE_IMAGE_LENGTH = 2_100_000;

    @Autowired
    private UserRepository repo;

    public User register(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Registration data is required");
        }

        if (user.getName() == null || user.getName().isBlank()
                || user.getEmail() == null || user.getEmail().isBlank()
                || user.getContactNo() == null || user.getContactNo().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Name, email, contact number and password are required");
        }

        user.setProfileImage(normalizeProfileImage(user.getProfileImage(), true));

        String normalizedEmail = user.getEmail().trim().toLowerCase(Locale.ROOT);
        if (repo.findByEmail(normalizedEmail) != null) {
            throw new IllegalArgumentException("Email is already registered");
        }

        user.setName(user.getName().trim());
        user.setEmail(normalizedEmail);
        user.setContactNo(user.getContactNo().trim());
        user.setId(null);
        // Registration never grants admin privileges. Admin role is set manually in DB.
        user.setRole("USER");
        return repo.save(user);
    }

    public User login(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        User user = repo.findByEmail(email.trim().toLowerCase(Locale.ROOT));
        if (user != null && password.equals(user.getPassword())) {
            return user;
        }
        return null;
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User id is required");
        }

        Optional<User> user = repo.findById(id);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return user.get();
    }

    public User updateProfile(Long userId, User payload) {
        User current = getById(userId);

        if (!isUser(current)) {
            throw new IllegalStateException("Only normal users can update this profile endpoint");
        }

        if (payload == null) {
            throw new IllegalArgumentException("Profile data is required");
        }

        if (payload.getName() != null && !payload.getName().isBlank()) {
            current.setName(payload.getName().trim());
        }

        if (payload.getEmail() != null && !payload.getEmail().isBlank()) {
            String normalizedEmail = payload.getEmail().trim().toLowerCase(Locale.ROOT);
            User existing = repo.findByEmail(normalizedEmail);
            if (existing != null && !existing.getId().equals(current.getId())) {
                throw new IllegalArgumentException("Email is already registered");
            }
            current.setEmail(normalizedEmail);
        }

        if (payload.getPassword() != null && !payload.getPassword().isBlank()) {
            current.setPassword(payload.getPassword());
        }

        if (payload.getContactNo() != null && !payload.getContactNo().isBlank()) {
            current.setContactNo(payload.getContactNo().trim());
        }

        if (payload.getProfileImage() != null) {
            current.setProfileImage(normalizeProfileImage(payload.getProfileImage(), false));
        }

        // Role changes are intentionally ignored here.
        return repo.save(current);
    }

    public void deleteUserAsAdmin(Long adminId, Long targetUserId) {
        requireAdmin(adminId);
        if (targetUserId == null) {
            throw new IllegalArgumentException("Target user id is required");
        }
        if (adminId.equals(targetUserId)) {
            throw new IllegalArgumentException("Admin cannot delete own account");
        }

        User target = getById(targetUserId);
        if ("ADMIN".equalsIgnoreCase(target.getRole())) {
            throw new IllegalArgumentException("Admin accounts cannot be deleted here");
        }

        repo.deleteById(targetUserId);
    }

    public void requireAdmin(Long userId) {
        User user = getById(userId);
        if (!isAdmin(user)) {
            throw new IllegalStateException("Admin access required");
        }
    }

    public void requireUser(Long userId) {
        User user = getById(userId);
        if (!isUser(user)) {
            throw new IllegalStateException("User access required");
        }
    }

    private boolean isAdmin(User user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }

    private boolean isUser(User user) {
        return user != null && "USER".equalsIgnoreCase(user.getRole());
    }

    private String normalizeProfileImage(String profileImage, boolean required) {
        if (profileImage == null || profileImage.isBlank()) {
            if (required) {
                throw new IllegalArgumentException("Profile picture is required");
            }
            return null;
        }

        String normalized = profileImage.trim();
        if (!normalized.startsWith("data:image/")) {
            throw new IllegalArgumentException("Profile picture must be a valid image");
        }

        if (normalized.length() > MAX_PROFILE_IMAGE_LENGTH) {
            throw new IllegalArgumentException("Profile picture is too large");
        }

        return normalized;
    }
}
