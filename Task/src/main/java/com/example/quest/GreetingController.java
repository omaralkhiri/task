package com.example.quest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {
    private final String name;

    public GreetingController(@Value("${YOUR_NAME:traveler}") String name) {
        this.name = name.isBlank() ? "traveler" : name;
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String greeting() {
        return "hello " + name;
    }
}
