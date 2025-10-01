package com.akshay.ecommerce.config;

import jakarta.validation.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MetadataValueNotNullValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MetadataValueNotNull {
    String message() default "Metadata values can not left blank or size more than 100 characters and they shoud be unique";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}