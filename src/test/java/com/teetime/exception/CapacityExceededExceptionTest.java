package com.teetime.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CapacityExceededExceptionTest {

    @Test
    void constructor_setsMessage() {
        String msg = "Capacity exceeded!";
        CapacityExceededException ex = new CapacityExceededException(msg);

        assertEquals(msg, ex.getMessage());
        assertTrue(ex instanceof Exception);
    }
}
