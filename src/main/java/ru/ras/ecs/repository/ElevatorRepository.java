package ru.ras.ecs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ras.ecs.entity.ElevatorEntity;

import java.util.Date;

@Repository
public interface ElevatorRepository extends JpaRepository<ElevatorEntity, Long> {

    @Transactional
    @Modifying
    @Query("delete from ElevatorEntity where callTime < ?1")
    void deleteByCallTime(Date time);

}
