package com.marcel.sorter.repository;

import com.marcel.sorter.domain.Sample;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleRepository extends JpaRepository<Sample, Long> {
}
