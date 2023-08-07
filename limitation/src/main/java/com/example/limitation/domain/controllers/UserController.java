package com.example.limitation.domain.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(name = "v1")
public class UserController {

    @GetMapping("/route1")
    public ResponseEntity<Object> getMessageOne() {
        return ResponseEntity.status(HttpStatus.OK).body("successfully get first message.");
    }

    @GetMapping("/route2")
    public ResponseEntity<Object> getMessageTwo() {
        return ResponseEntity.status(HttpStatus.OK).body("successfully get two message.");
    }
}
