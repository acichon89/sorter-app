package com.marcel.sorter.repository;

import com.marcel.sorter.domain.Rack;
import com.marcel.sorter.domain.VisionDefect;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RackRepository extends JpaRepository<Rack, Long> {

    @Query("SELECT distinct r.id FROM Rack r LEFT JOIN r.samples " +
            "WHERE SIZE(r.samples) = 0 OR (SIZE(r.samples) < r.capacity AND NOT EXISTS (" +
            "    SELECT 1 FROM Sample s " +
            "    WHERE r.id = s.rack.id " +
            "    AND (s.patientAge =:patientAge OR s.patientCompany =:patientCompany " +
            "           OR s.patientCityDistrict=:patientCityDistrict OR s.patientVisionDefect =:patientVisionDefect)" +
            "))")
    List<Long> findRacksNotHavingAgeCompanyDistrictAndVisionDefect(@Param("patientAge") int age,
                                                                   @Param("patientCompany") String patientCompany,
                                                                   @Param("patientCityDistrict") String patientCityDistrict,
                                                                   @Param("patientVisionDefect") VisionDefect patientVisionDefect);

    @Query("SELECT r FROM Rack r left join fetch r.samples where r.id=:id")
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<Rack> findWithLockingById(Long id);
}
