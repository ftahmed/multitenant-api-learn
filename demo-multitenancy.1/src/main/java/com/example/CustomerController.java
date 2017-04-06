package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    CustomerRepository repository;

    @PostMapping("/save")
    public String process() {
        repository.save(new Customer("Jack", "Smith"));
        repository.save(new Customer("Adam", "Johnson"));
        repository.save(new Customer("Kim", "Smith"));
        repository.save(new Customer("David", "Williams"));
        repository.save(new Customer("Peter", "Davis"));
        return "Done";
    }

    @GetMapping("/findall")
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        repository.findAll().forEach(customers::add);
        log.info("customers: {}", customers);
        return customers;
    }

    @GetMapping("/{tenantid}/findall")
    public List<Customer> findAllForATenant(@PathVariable String tenantid) {
        List<Customer> customers = new ArrayList<>();
        repository.findAll().forEach(customers::add);
        log.info("tenantid: {}, customers: {}", tenantid, customers);
        return customers;
    }

    @GetMapping("/findbyid")
    public Customer findById(@RequestParam("id") long id) {
        Optional<Customer> customer = repository.findOne(id);
        log.info("customer: {}", customer.get());
        return customer.get();
    }

    @GetMapping("/findbylastname")
    public List<Customer> fetchDataByLastName(@RequestParam("lastname") String lastName) {
        List<Customer> customers = new ArrayList<>();
        repository.findByLastName(lastName).forEach(customers::add);
        log.info("customers: {}", customers);
        return customers;
    }

    private CompletableFuture<List<Customer>> future = null;

    @GetMapping("/findallasync")
    public ResponseEntity<List<Customer>> findAllAsync() {
        ResponseEntity entity = null;

        if (future == null) {
            // no previous query
            future = repository.findAllCustomers();
            entity = new ResponseEntity(HttpStatus.ACCEPTED);

        } else if (future.isDone()) {
            // pending results
            try {
                Thread.sleep(2000L);
                List<Customer> customers = future.get();
                log.info("customers: {}", customers);
                entity = new ResponseEntity(customers, HttpStatus.OK);
                future = null;
            } catch (Exception e) {
                log.warn("Failed", e);
            }
        } else {
            entity = new ResponseEntity(HttpStatus.CONTINUE);
        }

        return entity;
    }

    @GetMapping("/findallasync2")
    public ResponseEntity<List<Customer>> findAllAsync2() {
        final ResponseEntity[] entity = {new ResponseEntity(HttpStatus.ACCEPTED)};

        future = repository.findAllCustomers();
        future.thenAccept(customers -> {
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                log.info("Can't sleep!", e);
            }
            log.info("customers: {}", customers);
            entity[0] = new ResponseEntity(customers, HttpStatus.OK);
        });
        return entity[0];
    }
}