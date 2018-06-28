package com.example.demo.schedulerTask;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerTask {

    private int count = 0;

    @Scheduled(fixedRate = 1000)
    private void process(){
        System.out.println("this is scheduler task runing  "+(count++));
    }

}
