package com.marcel.sorter.service;

import com.marcel.sorter.domain.Rack;
import com.marcel.sorter.domain.Sample;
import com.marcel.sorter.domain.VisionDefect;
import com.marcel.sorter.repository.RackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SorterServiceTest {

    private RackRepository rackRepository;
    private SorterService sorterService;

    @BeforeEach
    void setUp() {
        rackRepository = mock(RackRepository.class);
        sorterService = new SorterService(rackRepository);
    }

    @Test
    void shouldAssignSample() throws NoRackAvailableException {
        //given
        Sample sample = mockSample();
        when(rackRepository.findRacksNotHavingAgeCompanyDistrictAndVisionDefect(anyInt(), anyString(), anyString(), any(VisionDefect.class)))
                .thenReturn(List.of(1L));
        Rack rack = new Rack();
        rack.setSamples(new ArrayList<>());
        rack.setCapacity(20);
        rack.setId(1L);
        when(rackRepository.findWithLockingById(1L)).thenReturn(Optional.of(rack));
        when(rackRepository.save(any(Rack.class))).thenReturn(savedRackMock(1L, 8L));
        //when
        Assignment assignment = sorterService.assign(sample);
        assertAll(
                () -> assertNotNull(assignment),
                () -> assertEquals(1L, assignment.getRackId()),
                () -> assertEquals(8L, assignment.getSampleId())
        );
    }

    @Test
    void shouldThrowErrorForNoneRackAvailable() {
        //given
        Sample sample = mockSample();
        when(rackRepository.findRacksNotHavingAgeCompanyDistrictAndVisionDefect(anyInt(), anyString(), anyString(), any(VisionDefect.class)))
                .thenReturn(Collections.emptyList());
        //when
        NoRackAvailableException exc = assertThrows(NoRackAvailableException.class, () -> sorterService.assign(sample));
        //then
        assertEquals("Could not find any available rack for given params", exc.getMessage());
    }

    @Test
    void shouldThrowErrorForRackContainingSameData() {
        //given
        Sample sample = mockSample();
        when(rackRepository.findRacksNotHavingAgeCompanyDistrictAndVisionDefect(anyInt(), anyString(), anyString(), any(VisionDefect.class)))
                .thenReturn(List.of(1L));
        Rack rack = new Rack();
        rack.setCapacity(20);
        rack.setSamples(List.of(sample));
        when(rackRepository.findWithLockingById(1L)).thenReturn(Optional.of(rack));
        //when
        ObjectOptimisticLockingFailureException exc = assertThrows(ObjectOptimisticLockingFailureException.class, () -> sorterService.assign(sample));
        //then
        assertEquals("Object of class [com.marcel.sorter.domain.Rack] with identifier [Rack(id=null, capacity=20, " +
                "version=0, samples=[Sample(id=null, patientAge=50, patientCompany=Facebook, patientCityDistrict=NY Brooklyn, " +
                "patientVisionDefect=ASTIGMATISM, version=0)])]: optimistic locking failed", exc.getMessage());

    }

    private static Sample mockSample() {
        Sample sample = new Sample();
        sample.setPatientCompany("Facebook");
        sample.setPatientAge(50);
        sample.setPatientCityDistrict("NY Brooklyn");
        sample.setPatientVisionDefect(VisionDefect.ASTIGMATISM);
        return sample;
    }

    private Rack savedRackMock(Long rackId, Long sampleId) {
        Rack rack = new Rack();
        rack.setId(rackId);
        Sample sample = new Sample();
        sample.setId(sampleId);
        rack.setSamples(List.of(sample));
        return rack;
    }
}