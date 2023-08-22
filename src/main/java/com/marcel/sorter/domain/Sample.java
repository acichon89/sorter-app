package com.marcel.sorter.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Entity
@Table(name = "samples")
@Getter @Setter
@ToString(exclude = "rack")
public class Sample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "patient_age")
    private int patientAge;
    @Column(name = "patient_company")
    private String patientCompany;
    @Column(name = "patient_city_district")
    private String patientCityDistrict;
    @Column(name = "patient_vision_defect")
    @Enumerated(EnumType.STRING)
    private VisionDefect patientVisionDefect;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rack_id")
    private Rack rack;

    @Version
    private int version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sample sample = (Sample) o;
        return Objects.equals(id, sample.getId());
    }


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
