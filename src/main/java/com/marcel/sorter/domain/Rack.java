package com.marcel.sorter.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "racks")
@Getter @Setter
@ToString
public class Rack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int capacity;
    @Version
    private int version;

    public boolean contains(Sample sample) {
        return this.samples.stream().anyMatch(s -> s.getPatientAge() == sample.getPatientAge() || s.getPatientVisionDefect() == sample.getPatientVisionDefect() ||
                s.getPatientCityDistrict().equals(sample.getPatientCityDistrict()) || s.getPatientCompany().equals(sample.getPatientCompany()));
    }

    @OneToMany(mappedBy = "rack", cascade = CascadeType.ALL)
    private List<Sample> samples;

    public void addSample(Sample sample) {
        sample.setRack(this);
        samples.add(sample);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rack rack = (Rack) o;
        return Objects.equals(id, rack.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
