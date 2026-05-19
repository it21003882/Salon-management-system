package com.salon.beautysalon.dto.request;

public record UserUpdateRequest(String name, String email, String contactNo, String password, String profileImage) {
}
