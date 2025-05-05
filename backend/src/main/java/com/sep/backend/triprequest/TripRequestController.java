package com.sep.backend.triprequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TripRequestController {

    @GetMapping("/search")
    public String search() {
        return "search";
    }
}
