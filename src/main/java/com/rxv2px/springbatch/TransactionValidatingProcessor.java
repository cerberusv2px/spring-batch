package com.rxv2px.springbatch;

import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionValidatingProcessor extends ValidatingItemProcessor<Customer> {
    public TransactionValidatingProcessor(final int limit) {
        super(
            item -> {
                if (item.getTransactions() >= limit) {
                    throw new ValidationException("Customer has more than " + limit + " transactions");
                }
                log.info("Customer {} matched the transaction filter", item);
            }
        );
        setFilter(true);
    }
}
