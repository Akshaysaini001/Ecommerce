package com.akshay.ecommerce.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserInactiveTest {
    @Test
    void newCustomer_shouldBeInactiveByDefault() {
        Customer c = new Customer();
        assertFalse(c.isActive());
    }
}
