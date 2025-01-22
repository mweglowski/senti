package org.example.webapi.controller;

import org.apache.coyote.Response;
import org.example.webapi.contract.LogDTO;
import org.example.webapi.service.LogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/logs")
public class LogController {
    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping
    public ResponseEntity<List<LogDTO>> getLogs() {
        List<LogDTO> logs = logService.getLogs();

        return new ResponseEntity<>(logs, HttpStatus.OK);
    }
}
