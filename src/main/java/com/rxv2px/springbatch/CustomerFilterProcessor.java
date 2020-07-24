package com.rxv2px.springbatch;

import org.springframework.batch.item.ItemProcessor;

import java.util.Calendar;
import java.util.GregorianCalendar;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerFilterProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(final Customer item) {
        if (new GregorianCalendar().get(Calendar.MONTH) == item.getBirthday().get(Calendar.MONTH)) {
            log.info("Customer {} matched the birthday filter", item);
            return item;
        }
        return null;
    }
}
