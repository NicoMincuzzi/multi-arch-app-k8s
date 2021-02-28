package com.nmincuzzi.controller;

import com.nmincuzzi.controller.representation.DummyRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class DummyController {

    @GetMapping("/test")
    public ResponseEntity<DummyRepresentation> getDummy() {
        DummyRepresentation statusRepresentation = new DummyRepresentation(HttpStatus.OK.value(), "I'm here!");
        Optional<DummyRepresentation> body = Optional.of(statusRepresentation);
        return ResponseEntity.of(body);
    }
}
