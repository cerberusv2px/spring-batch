package com.rxv2px.springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.beans.XMLEncoder;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling
@EnableBatchProcessing
@SpringBootApplication
public class SpringbatchApplication {

    public static void main(String[] args) {
        prepareData(1000);
        SpringApplication.run(SpringbatchApplication.class, args);
    }

    private static void prepareData(final int amount) {
        final int actualYear = new GregorianCalendar().get(Calendar.YEAR);
        final Collection<Customer> customers = new LinkedList<>();
        for (int i = 0; i < amount; i++) {
            final Calendar birthday = new GregorianCalendar();
            birthday.set(Calendar.YEAR, random(actualYear - 100, actualYear));
            birthday.set(Calendar.DAY_OF_YEAR, random(1, birthday.getActualMaximum(Calendar.DAY_OF_YEAR)));

            final Customer customer = new Customer();
            customer.setId(i + 1);
            customer.setName(UUID.randomUUID().toString().replaceAll("[^a-z]]", ""));
            customer.setBirthday(birthday);
            customer.setTransactions(random(0, 100));
            customers.add(customer);
        }

        try (final XMLEncoder encoder = new XMLEncoder(new FileOutputStream(CustomerReportJobConfig.XML_FILE))) {
            encoder.writeObject(customers);
        } catch (final FileNotFoundException e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    private static int random(final int start, final int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }

}
