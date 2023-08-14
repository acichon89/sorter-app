package com.marcel.sorter.controller;

import com.marcel.sorter.domain.Sample;
import com.marcel.sorter.service.Assignment;
import com.marcel.sorter.service.NoRackAvailableException;
import com.marcel.sorter.service.SorterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SorterController {

    @Autowired
    private SorterService sorterService;

    @PostMapping(value = "/add")
    public ResponseEntity<Assignment> add(@RequestBody AssignRequestDTO dto) throws NoRackAvailableException {
        return ResponseEntity.ok(this.sorterService.assign(map(dto)));
    }

    private Sample map(AssignRequestDTO dto) {
        Sample sample = new Sample();
        sample.setPatientAge(dto.getPatientAge());
        sample.setPatientVisionDefect(dto.getPatientVisionDefect());
        sample.setPatientCityDistrict(dto.getPatientCityDistrict());
        sample.setPatientCompany(dto.getPatientCompany());
        return sample;
    }
}
