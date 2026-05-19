package com.salon.beautysalon.controller;

import com.salon.beautysalon.dto.request.ServiceRequest;
import com.salon.beautysalon.dto.response.ServiceResponse;
import com.salon.beautysalon.model.Service;
import com.salon.beautysalon.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/services")
public class ServiceController {

    @Autowired
    private ServiceService service;

    @PostMapping
    public ResponseEntity<?> add(@RequestParam Long adminId, @RequestBody ServiceRequest request) {
        try {
            Service saved = service.addServiceAsAdmin(adminId, toEntity(request));
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(ex.getMessage()));
        }
    }

    @GetMapping
    public List<ServiceResponse> getAll() {
        return service.getAll().stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestParam Long adminId, @RequestBody ServiceRequest request) {
        try {
            Service updated = service.updateServiceAsAdmin(adminId, id, toEntity(request));
            return ResponseEntity.ok(toResponse(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @RequestParam Long adminId) {
        try {
            service.deleteAsAdmin(adminId, id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(ex.getMessage()));
        }
    }

    private Service toEntity(ServiceRequest request) {
        Service entity = new Service();
        if (request != null) {
            entity.setName(request.name());
            entity.setPrice(request.price());
        }
        return entity;
    }

    private ServiceResponse toResponse(Service service) {
        return new ServiceResponse(service.getId(), service.getName(), service.getPrice());
    }

    private Map<String, String> error(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("message", message);
        return body;
    }
}
