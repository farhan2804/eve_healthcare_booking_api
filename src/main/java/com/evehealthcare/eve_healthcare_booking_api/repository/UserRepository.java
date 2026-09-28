package com.evehealthcare.eve_healthcare_booking_api.repository;

import java.util.Optional;
import com.evehealthcare.eve_healthcare_booking_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}