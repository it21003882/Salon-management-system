package com.salon.beautysalon.dto.response;

public record AppointmentResponse(
	Long id,
	Long userId,
	Long serviceId,
	String date,
	String userName,
	String serviceName
) {
}
