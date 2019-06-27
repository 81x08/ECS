package ru.ras.ecs.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "elevator")
public class ElevatorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "last_visit_floor", nullable = false)
    private Integer lastVisitFloor;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private StateEntity state;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "floors",
            joinColumns = @JoinColumn(name = "elevator_id")
    )
    private Set<Integer> floors;

    public ElevatorEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLastVisitFloor() {
        return lastVisitFloor;
    }

    public void setLastVisitFloor(Integer lastVisitFloor) {
        this.lastVisitFloor = lastVisitFloor;
    }

    public StateEntity getState() {
        return state;
    }

    public void setState(StateEntity state) {
        this.state = state;
    }

    public Set<Integer> getFloors() {
        return floors;
    }

    public void setFloors(Set<Integer> floors) {
        this.floors = floors;
    }

    @Override
    public String toString() {
        return "ElevatorEntity{" +
                "id=" + id +
                ", lastVisitFloor=" + lastVisitFloor +
                ", state=" + state +
                ", floors=" + floors +
                '}';
    }

}
