package ru.clevertec.reflectivejson.entity;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ToString
@Data
public class Customer {
    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate dateBirth;
    private List<Order> orders;
}
