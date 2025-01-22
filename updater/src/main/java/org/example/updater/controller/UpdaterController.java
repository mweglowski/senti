package org.example.updater.controller;

import org.example.updater.service.UpdaterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updater")
public class UpdaterController {
    private final UpdaterService updaterService;

    public UpdaterController(UpdaterService updaterService) {
        this.updaterService = updaterService;
    }

    @GetMapping("/{subreddit}")
    public ResponseEntity<String> updateBySubreddit(@PathVariable String subreddit) {
        String successMessage = updaterService.updateBySubreddit(subreddit);

        return new ResponseEntity<>(successMessage, HttpStatus.OK);
    }
}
