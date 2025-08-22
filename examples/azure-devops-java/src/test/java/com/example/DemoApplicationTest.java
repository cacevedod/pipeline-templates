package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DemoApplicationTest {
    @Test
    void mainNoLanzaExcepciones() {
        assertDoesNotThrow(() -> DemoApplication.main(new String[]{}));
    }
}
