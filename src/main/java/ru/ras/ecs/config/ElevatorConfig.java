package ru.ras.ecs.config;

public class ElevatorConfig {

    /**
     * Время проезда между двумя этажами
     */
    public static final int TIME_SPEED_FLOOR = 10;

    /**
     * Время разгона лифта
     */
    public static final int SPEED_RUN = 2;

    /**
     * Время торможения лифта
     */
    public static final int SPEED_STOP = 2;

    /**
     * Считаем количество времени, которое потребуется лифту, чтобы проехать
     * с этажа А до Б
     * @param floorA Этаж А
     * @param floorB Этаж Б
     * @return Время
     */
    public static long getTimeBetweenFloors(int floorA, int floorB) {
        return (Math.abs(floorA - floorB) * TIME_SPEED_FLOOR + SPEED_RUN + SPEED_STOP) * 1000;
    }

    /**
     * Общее время, которое должен проехать лифт, что попасть с А этажа на Б
     * @return Время
     */
    public static long getTimes() {
        return (TIME_SPEED_FLOOR + SPEED_RUN + SPEED_STOP) * 1000;
    }

}
