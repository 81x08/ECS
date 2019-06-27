package ru.ras.ecs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.ras.ecs.config.ElevatorConfig;
import ru.ras.ecs.entity.ElevatorEntity;
import ru.ras.ecs.entity.StateEntity;
import ru.ras.ecs.repository.ElevatorRepository;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.TreeSet;

@Component
@Scope("singleton")
public class ElevatorWorkingComponent extends Thread {

    @Autowired
    private ElevatorRepository elevatorRepository;

    /**
     * Последний посещённый этаж
     */
    private int lastVisitFloor = 0;

    /**
     * Следующий этаж
     */
    private volatile int nextFloor = 0;

    /**
     * Время, когда лифт приедет к nextFloor этажу
     */
    private volatile long timeNextFloor = 0;

    /**
     * В каком направление двигается лифт
     */
    private StateEntity stateElevator;

    /**
     * Этажи, к которым был вызван лифт
     */
    private volatile TreeSet<Integer> queueFloors = new TreeSet<>();

    @PostConstruct
    public void postConstruct() {
        final Optional<ElevatorEntity> optionalElevatorEntity = elevatorRepository.findById(1L);

        if (optionalElevatorEntity.isPresent()) {
            final ElevatorEntity elevatorEntity = optionalElevatorEntity.get();

            lastVisitFloor = elevatorEntity.getLastVisitFloor();
            stateElevator = elevatorEntity.getState();

            queueFloors.addAll(elevatorEntity.getFloors());
        }

        start();
    }

    @Override
    public void run() {
        while (true) {
            if (queueFloors.isEmpty()) {
                nextFloor = lastVisitFloor;
            } else {
                long currentTime = System.currentTimeMillis();

                if (currentTime >= timeNextFloor) {
                    queueFloors.remove(lastVisitFloor);

                    lastVisitFloor = nextFloor;

                    if (!queueFloors.isEmpty()) {
                        nextFloor = nextFloor();

                        if (lastVisitFloor != nextFloor) {
                            timeNextFloor = currentTime + ElevatorConfig.getTimeBetweenFloors(lastVisitFloor, nextFloor);
                        }

                        Optional<ElevatorEntity> optionalElevatorEntity = elevatorRepository.findById(1L);

                        if (optionalElevatorEntity.isPresent()) {
                            ElevatorEntity elevatorEntity = optionalElevatorEntity.get();

                            elevatorEntity.setLastVisitFloor(lastVisitFloor);
                            elevatorEntity.setState(stateElevator);
                            elevatorEntity.setFloors(queueFloors);

                            elevatorRepository.save(elevatorEntity);
                        }
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String addFloor(int floor) {
        if (queueFloors.contains(floor)) {
            return "Лифт едет к Вам.";
        }

        queueFloors.add(floor);

        if (queueFloors.isEmpty()) {
            nextFloor = nextFloor();

            timeNextFloor = System.currentTimeMillis() + ElevatorConfig.getTimeBetweenFloors(lastVisitFloor, nextFloor);
        } else {
            long timeBetweenFloors = ElevatorConfig.getTimeBetweenFloors(floor, nextFloor);

            long timeCurrentFloors = (timeNextFloor - System.currentTimeMillis());

            if (timeCurrentFloors > timeBetweenFloors) {
                nextFloor = nextFloor();

                timeNextFloor = System.currentTimeMillis() + ElevatorConfig.getTimeBetweenFloors(lastVisitFloor, nextFloor);
            }
        }

        Optional<ElevatorEntity> optionalElevatorEntity = elevatorRepository.findById(1L);

        if (optionalElevatorEntity.isPresent()) {
            ElevatorEntity elevatorEntity = optionalElevatorEntity.get();

            elevatorEntity.setLastVisitFloor(lastVisitFloor);
            elevatorEntity.setState(stateElevator);
            elevatorEntity.setFloors(queueFloors);

            elevatorRepository.save(elevatorEntity);
        }

        return "Лифт едет к Вам.";
    }

    public String stateElevator() {
        if (lastVisitFloor == nextFloor) {
            return "Лифт находится на " + lastVisitFloor + " этаже.";
        }

        return "Лифт едет с " + lastVisitFloor + "го на " + nextFloor + "й этаж.";
    }

    /**
     * Вызвали ли этаж, после текущего
     * @return
     */
    private int nextFloor() {
        Integer nextFloor;

        // Если лифт двигается ВВЕРХ
        if (stateElevator == StateEntity.UP) {
            // Проверяем, есть ли следующий вызванный этаж
            if (queueFloors.ceiling(lastVisitFloor) != null) {
                // Да, есть. Значит едем к нему, без изменения направления
                nextFloor = queueFloors.ceiling(lastVisitFloor);
            } else {
                // Нет, нету. Значит едем вниз, изменив направление
                nextFloor = queueFloors.floor(lastVisitFloor);

                stateElevator = StateEntity.DOWN;
            }
        } else {
            // Если лифт двигается ВНИЗ
            // Проверяем, есть ли следующий вызванный этаж
            if (queueFloors.floor(lastVisitFloor) != null) {
                // Да, есть. Значит едем к нему, без изменения направления
                nextFloor = queueFloors.floor(lastVisitFloor);
            } else {
                // Нет, нету. Значит едем наверх, изменив направление
                nextFloor = queueFloors.ceiling(lastVisitFloor);

                stateElevator = StateEntity.UP;
            }
        }

        // Если следующий этаж null, это значит, что других вызванных этажей нет
        if (nextFloor == null) {
            // Следовательно, мы стоим на текущем этаже
            nextFloor = lastVisitFloor;
        }

        return nextFloor;
    }

}
