package org.example.spring_security.repository;
import org.example.spring_security.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserData,Long> {
    Optional<UserData>findByEmail(String email);
}
