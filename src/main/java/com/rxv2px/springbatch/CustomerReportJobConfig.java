package com.rxv2px.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class CustomerReportJobConfig {

    public static final String TASKLET_STEP = "taskletStep";

    public static final String XML_FILE = "database.xml";

    private static final String JOB_NAME = "customerReportJob";

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;


    @Scheduled(fixedRate = 5000)
    public void run() throws Exception {
        JobExecution execution = jobLauncher.run(
            customerReportJob(),
            new JobParametersBuilder().addLong("uniqueness", System.nanoTime()).toJobParameters()
        );
        log.info("Exit status: {}", execution.getStatus());
    }

    @Bean
    public Job customerReportJob() {
        return jobBuilders.get(JOB_NAME)
            .start(taskletStep())
            .next(chunkStep())
            .build();
    }

    @Bean
    public Step taskletStep() {
        return stepBuilders.get(TASKLET_STEP)
            .tasklet(tasklet())
            .build();
    }

    @Bean
    public Step chunkStep() {
        return stepBuilders.get("chunkStep")
            .<Customer, Customer>chunk(20)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build();
    }

    @StepScope
    @Bean
    public ItemReader<Customer> reader() {
        return new CustomerItemReader(XML_FILE);
    }

    @StepScope
    @Bean
    public ItemProcessor<Customer, Customer> processor() {
        final CompositeItemProcessor<Customer, Customer> processor = new CompositeItemProcessor<>();
        processor.setDelegates(Arrays.asList(birthdayFilterProcessor(), transactionValidatingProcessor()));
        return processor;
    }

    @StepScope
    @Bean
    public CustomerFilterProcessor birthdayFilterProcessor() {
        return new CustomerFilterProcessor();
    }

    @StepScope
    @Bean
    public TransactionValidatingProcessor transactionValidatingProcessor() {
        return new TransactionValidatingProcessor(5);
    }

    @StepScope
    @Bean
    public ItemWriter<Customer> writer() {
        return new CustomerItemWriter();
    }

    @Bean
    public Tasklet tasklet() {
        return (contribution, chunkContext) -> {
            log.info("Executing tasklet step");
            return RepeatStatus.FINISHED;
        };
    }

}
