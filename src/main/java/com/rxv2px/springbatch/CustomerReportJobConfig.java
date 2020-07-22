package com.rxv2px.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerReportJobConfig {

    public static final String TASKLET_STEP = "taskletStep";

    public static final String XML_FILE = "database.xml";

    private static final String JOB_NAME = "customerReportJob";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job customerReportJob() {
        return jobBuilderFactory.get("customerReportJob")
                .start(taskletStep())
                // .next(chunkStep())
                .build();
    }

    @Bean
    public Step taskletStep() {
        return stepBuilderFactory.get("taskletStep")
                .tasklet(tasklet())
                .build();
    }


    public Tasklet tasklet() {
        return (stepContribution, chunkContext) -> RepeatStatus.FINISHED;
    }
}
