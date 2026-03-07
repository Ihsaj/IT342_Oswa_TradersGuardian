package edu.cit.oswa.tradersguardian.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.oswa.tradersguardian.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}