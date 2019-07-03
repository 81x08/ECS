package ru.ras.ecs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ras.ecs.entity.ElevatorEntity;

@Repository
public interface ElevatorRepository extends JpaRepository<ElevatorEntity, Long> {
}
