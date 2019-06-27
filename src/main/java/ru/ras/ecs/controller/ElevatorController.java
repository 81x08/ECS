package ru.ras.ecs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.ras.ecs.service.ElevatorService;

@Controller
@RequestMapping("/elevator")
public class ElevatorController {

    @Autowired
    private ElevatorService elevatorService;

    @GetMapping("call/{floor}")
    public ResponseEntity<String> call(@PathVariable int floor) {
        return new ResponseEntity<>(elevatorService.call(floor), HttpStatus.OK);
    }

    @GetMapping("get")
    public ResponseEntity<String> get() {
        return new ResponseEntity<>(elevatorService.get(), HttpStatus.OK);
    }

}
