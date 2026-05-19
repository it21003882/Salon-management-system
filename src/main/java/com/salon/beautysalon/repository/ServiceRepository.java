package com.salon.beautysalon.repository;

import com.salon.beautysalon.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
}