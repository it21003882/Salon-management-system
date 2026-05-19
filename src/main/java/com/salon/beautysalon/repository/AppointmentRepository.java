package com.salon.beautysalon.repository;

import com.salon.beautysalon.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
	List<Appointment> findByUserId(Long userId);
}