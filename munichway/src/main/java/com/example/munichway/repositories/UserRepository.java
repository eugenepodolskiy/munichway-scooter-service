package com.example.munichway.repositories;

import com.example.munichway.models.Trip;
import com.example.munichway.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


}
