package com.salon.beautysalon.service;

import com.salon.beautysalon.model.Appointment;
import com.salon.beautysalon.model.User;
import com.salon.beautysalon.repository.AppointmentRepository;
import com.salon.beautysalon.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository repo;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserService userService;

    public Appointment bookAsUser(Long userId, Appointment a) {
        if (a == null || a.getServiceId() == null || a.getDate() == null || a.getDate().isBlank()) {
            throw new IllegalArgumentException("Service and date are required");
        }

        User user = userService.getById(userId);
        if (!"USER".equalsIgnoreCase(user.getRole())) {
            throw new IllegalStateException("User access required");
        }

        com.salon.beautysalon.model.Service service = serviceRepository.findById(a.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        if (service.getId() == null) {
            throw new IllegalArgumentException("Service not found");
        }

        // Always bind the appointment to the authenticated user.
        a.setUserId(userId);
        a.setUserName(user.getName());
        a.setServiceName(service.getName());
        return repo.save(a);
    }

    public List<Appointment> getAllForAdmin(Long adminId) {
        userService.requireAdmin(adminId);
        return repo.findAll();
    }

    public List<Appointment> getUserAppointments(Long userId) {
        userService.requireUser(userId);
        return repo.findByUserId(userId);
    }

    public Appointment rescheduleAsUser(Long userId, Long appointmentId, Appointment payload) {
        userService.requireUser(userId);
        Appointment existing = repo.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (!userId.equals(existing.getUserId())) {
            throw new IllegalStateException("You can only reschedule your own appointments");
        }

        if (payload == null || payload.getDate() == null || payload.getDate().isBlank()) {
            throw new IllegalArgumentException("New date is required");
        }

        existing.setDate(payload.getDate());
        return repo.save(existing);
    }

    public void cancelAsUser(Long userId, Long appointmentId) {
        userService.requireUser(userId);
        Appointment existing = repo.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (!userId.equals(existing.getUserId())) {
            throw new IllegalStateException("You can only cancel your own appointments");
        }

        repo.deleteById(appointmentId);
    }
}
