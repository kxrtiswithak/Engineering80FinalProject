package com.sparta.eng80.onetoonetracker.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootTest
public class ErrorControllerTest {

    @Autowired
    private ErrorController errorController;

    @Test
    public void loadsContext() {
        Assertions.assertNotNull(errorController);
    }
}
