package com.salon.beautysalon.service;

import com.salon.beautysalon.model.Service;
import com.salon.beautysalon.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository repo;

    @Autowired
    private UserService userService;

    public Service addServiceAsAdmin(Long adminId, Service s) {
        userService.requireAdmin(adminId);
        validateServicePayload(s);
        return repo.save(s);
    }

    public List<Service> getAll() {
        return repo.findAll();
    }

    public Service updateServiceAsAdmin(Long adminId, Long serviceId, Service payload) {
        userService.requireAdmin(adminId);
        validateServicePayload(payload);

        Service existing = repo.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        existing.setName(payload.getName().trim());
        existing.setPrice(payload.getPrice());
        return repo.save(existing);
    }

    public void deleteAsAdmin(Long adminId, Long id) {
        userService.requireAdmin(adminId);
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Service not found");
        }
        repo.deleteById(id);
    }

    private void validateServicePayload(Service payload) {
        if (payload == null || payload.getName() == null || payload.getName().isBlank()) {
            throw new IllegalArgumentException("Service name is required");
        }
        if (payload.getPrice() <= 0) {
            throw new IllegalArgumentException("Service price must be greater than 0");
        }
    }
}