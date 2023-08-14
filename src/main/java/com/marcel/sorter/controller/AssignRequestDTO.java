package com.marcel.sorter.controller;

import com.marcel.sorter.domain.VisionDefect;
import lombok.Data;

@Data
public class AssignRequestDTO {
    private int patientAge;
    private String patientCompany;
    private String patientCityDistrict;
    private VisionDefect patientVisionDefect;
}
