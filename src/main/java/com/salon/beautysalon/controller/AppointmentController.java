package com.salon.beautysalon.controller;

import com.salon.beautysalon.dto.request.AppointmentCreateRequest;
import com.salon.beautysalon.dto.request.AppointmentRescheduleRequest;
import com.salon.beautysalon.dto.response.AppointmentResponse;
import com.salon.beautysalon.model.Appointment;
import com.salon.beautysalon.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService service;

    @PostMapping
    public ResponseEntity<?> book(@RequestParam Long userId, @RequestBody AppointmentCreateRequest request) {
        try {
            Appointment saved = service.bookAsUser(userId, toBookEntity(request));
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllForAdmin(@RequestParam Long adminId) {
        try {
            List<AppointmentResponse> appointments = service.getAllForAdmin(adminId)
                    .stream()
                    .map(this::toResponse)
                    .toList();
            return ResponseEntity.ok(appointments);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(ex.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyAppointments(@RequestParam Long userId) {
        try {
            List<AppointmentResponse> appointments = service.getUserAppointments(userId)
                    .stream()
                    .map(this::toResponse)
                    .toList();
            return ResponseEntity.ok(appointments);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> reschedule(@PathVariable Long id, @RequestParam Long userId, @RequestBody AppointmentRescheduleRequest request) {
        try {
            Appointment updated = service.rescheduleAsUser(userId, id, toRescheduleEntity(request));
            return ResponseEntity.ok(toResponse(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id, @RequestParam Long userId) {
        try {
            service.cancelAsUser(userId, id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(ex.getMessage()));
        }
    }

    private Appointment toBookEntity(AppointmentCreateRequest request) {
        Appointment appointment = new Appointment();
        if (request != null) {
            appointment.setServiceId(request.serviceId());
            appointment.setDate(request.date());
        }
        return appointment;
    }

    private Appointment toRescheduleEntity(AppointmentRescheduleRequest request) {
        Appointment appointment = new Appointment();
        if (request != null) {
            appointment.setDate(request.date());
        }
        return appointment;
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getUserId(),
                appointment.getServiceId(),
                appointment.getDate(),
                appointment.getUserName(),
                appointment.getServiceName()
        );
    }

    private Map<String, String> error(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("message", message);
        return body;
    }
}
