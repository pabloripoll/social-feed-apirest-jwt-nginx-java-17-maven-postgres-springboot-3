package com.restapi.domain.geo.repository;

import com.restapi.domain.geo.entity.Continent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContinentRepository extends JpaRepository<Continent, Long> {
    Optional<Continent> findByName(String name);
    boolean existsByName(String name);
}
