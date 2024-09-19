package ru.clevertec.reflectivejson.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class Product {
    private UUID id;
    private String name;
    private Double price;
}
