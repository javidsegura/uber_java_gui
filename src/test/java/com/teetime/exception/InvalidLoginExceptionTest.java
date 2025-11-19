package com.teetime.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidLoginExceptionTest {

    @Test
    void constructor_setsMessage() {
        String msg = "Invalid login";
        InvalidLoginException ex = new InvalidLoginException(msg);

        assertEquals(msg, ex.getMessage());
        assertTrue(ex instanceof Exception);
    }
}
