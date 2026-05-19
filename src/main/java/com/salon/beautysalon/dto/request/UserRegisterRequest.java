package com.salon.beautysalon.dto.request;

public record UserRegisterRequest(String name, String email, String contactNo, String password, String profileImage) {
}
