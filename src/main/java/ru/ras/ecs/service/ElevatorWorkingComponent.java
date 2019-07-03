package ru.ras.ecs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.ras.ecs.config.ElevatorConfig;
import ru.ras.ecs.entity.ElevatorEntity;
import ru.ras.ecs.entity.StateEntity;
import ru.ras.ecs.repository.ElevatorRepository;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Optional;
import java.util.TreeSet;

@Component
@Scope("singleton")
public class ElevatorWorkingComponent {

    @Autowired
    private ElevatorRepository elevatorRepository;

    private long timeLastFloor;

    private int currentFloor;

    private StateEntity stateElevator;

    private TreeSet<Integer> queueFloors = new TreeSet<>();

    @PostConstruct
    private void postConstruct() {
        final Optional<ElevatorEntity> optionalElevatorEntity = elevatorRepository.findById(1L);

        if (optionalElevatorEntity.isPresent()) {
            final ElevatorEntity elevatorEntity = optionalElevatorEntity.get();

            timeLastFloor = elevatorEntity.getTimeLastFloor().getTime();
            currentFloor = elevatorEntity.getLastVisitFloor();
            stateElevator = elevatorEntity.getState();

            queueFloors.addAll(elevatorEntity.getFloors());
        }
    }

    public synchronized String call(int floor) {
        if (queueFloors.isEmpty()) {
            timeLastFloor = System.currentTimeMillis();

            if (floor > currentFloor) {
                stateElevator = StateEntity.UP;
            } else {
                stateElevator = StateEntity.DOWN;
            }
        }

        queueFloors.add(floor);

        saveElevator();

        return "Лифт вызван.";
    }

    public synchronized String get() {
        if (queueFloors.isEmpty()) {
            return "Лифт сейчас на " + currentFloor + " этаже.";
        }

        long currentTime = System.currentTimeMillis();

        int countDrivenFloor = (int) Math.ceil(Math.abs(timeLastFloor - currentTime) / ElevatorConfig.getTimes());

        if (countDrivenFloor > 0) {
            timeLastFloor += (countDrivenFloor * ElevatorConfig.getTimes());

            while (countDrivenFloor > 0) {
                if (queueFloors.isEmpty()) {
                    break;
                }

                if (stateElevator == StateEntity.UP) {
                    currentFloor++;

                    if (currentFloor == queueFloors.last()) {
                        stateElevator = StateEntity.DOWN;
                    }
                } else {
                    currentFloor--;

                    if (currentFloor == queueFloors.first()) {
                        stateElevator = StateEntity.UP;
                    }
                }

                queueFloors.remove(currentFloor);

                countDrivenFloor--;

                saveElevator();
            }
        }

        if (queueFloors.isEmpty()) {
            return "Лифт сейчас на " + currentFloor + " этаже.";
        }

        return "Лифт сейчас едет с " + currentFloor + " на " + (stateElevator == StateEntity.UP ? queueFloors.last() : queueFloors.first()) + " этаж. " + queueFloors;
    }

    private synchronized void saveElevator() {
        Optional<ElevatorEntity> optionalElevatorEntity = elevatorRepository.findById(1L);

        if (optionalElevatorEntity.isPresent()) {
            ElevatorEntity elevatorEntity = optionalElevatorEntity.get();

            elevatorEntity.setTimeLastFloor(new Date(timeLastFloor));
            elevatorEntity.setLastVisitFloor(currentFloor);
            elevatorEntity.setState(stateElevator);
            elevatorEntity.setFloors(queueFloors);

            elevatorRepository.save(elevatorEntity);
        }
    }

}
