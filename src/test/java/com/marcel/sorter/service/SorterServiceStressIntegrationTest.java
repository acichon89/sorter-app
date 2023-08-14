package com.marcel.sorter.service;

import com.marcel.sorter.domain.Sample;
import com.marcel.sorter.domain.VisionDefect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SorterServiceStressIntegrationTest {

    private Collection<Assignment> assignments;

    @Autowired
    private SorterService sorterService;

    @BeforeEach
    void setUp() {
        assignments = Collections.synchronizedCollection(new ArrayList<>());
    }

    @Test
    void shouldPutNewSimilarDataToEachRack() throws InterruptedException {
        //given:
        int numberOfSamples = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfSamples);
        AtomicInteger noOfFailures = new AtomicInteger(0);
        for (int i = 0; i < numberOfSamples; i++) {
            executorService.execute(() -> {
                Sample sample = new Sample();
                sample.setPatientAge(18);
                sample.setPatientCityDistrict("NY Bronks");
                sample.setPatientCompany("Apple");
                sample.setPatientVisionDefect(VisionDefect.FARSIGHTED);
                try {
                    //when:
                    assignments.add(sorterService.assign(sample));
                } catch (NoRackAvailableException e) {
                    noOfFailures.incrementAndGet();
                }
            });
        }
        executorService.shutdown();
        //then:
        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));
        assertEquals(numberOfSamples, assignments.size());
        assertEquals(numberOfSamples, assignments.stream().map(Assignment::getRackId).collect(Collectors.toSet()).size());
        assertEquals(noOfFailures.get(), 0);
    }

    @Test
    void shouldPutNewSimilarDataToEachRack_for_restThrowNoAvailableException() throws InterruptedException {
        //given:
        int numberOfSamples = 15;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfSamples);
        AtomicInteger noOfFailures = new AtomicInteger(0);
        for (int i = 0; i < numberOfSamples; i++) {
            executorService.execute(() -> {
                Sample sample = new Sample();
                sample.setPatientAge(18);
                sample.setPatientCityDistrict("NY Bronks");
                sample.setPatientCompany("Apple");
                sample.setPatientVisionDefect(VisionDefect.FARSIGHTED);
                try {
                    //when:
                    assignments.add(sorterService.assign(sample));
                } catch (NoRackAvailableException e) {
                    noOfFailures.incrementAndGet();
                }
            });
        }
        executorService.shutdown();
        //then:
        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));
        assertEquals(5, assignments.size());
        assertEquals(5, assignments.stream().map(Assignment::getRackId).collect(Collectors.toSet()).size());
        assertEquals(noOfFailures.get(), 10);
    }
}