package ru.clevertec.reflectivejson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import ru.clevertec.reflectivejson.entity.Customer;
import ru.clevertec.reflectivejson.entity.Order;
import ru.clevertec.reflectivejson.entity.Product;
import ru.clevertec.reflectivejson.service.FromJsonConverter;
import ru.clevertec.reflectivejson.service.ToJsonConverter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class ReflectiveJsonApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReflectiveJsonApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(ToJsonConverter toJsonConverter, FromJsonConverter fromJsonConverter) {
        return args -> {

            Product product1 = new Product();
            product1.setId(UUID.randomUUID());
            product1.setName("Телефон");
            product1.setPrice(100.0);

            Product product2 = new Product();
            product2.setId(UUID.randomUUID());
            product2.setName("Машина");
            product2.setPrice(100.0);

            List<Product> products = Arrays.asList(product1, product2);

            Order order = new Order();
            order.setId(UUID.randomUUID());
            order.setProducts(products);
            order.setCreateDate(OffsetDateTime.parse("2023-10-24T17:50:30.5470749+03:00"));

            Customer customer = new Customer();
            customer.setId(UUID.randomUUID());
            customer.setFirstName("Reuben");
            customer.setLastName("Martin");
            customer.setDateBirth(LocalDate.of(2003, 11, 3));
            customer.setOrders(List.of(order));

            String json = toJsonConverter.toJson(customer);
            System.out.println("To json:" + json);

            Customer customerFromJson = fromJsonConverter.fromJson(json, Customer.class);
            System.out.println("FromJson: " + customerFromJson);
        };
    }
}
