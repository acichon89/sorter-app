package com.marcel.sorter.service;

import com.marcel.sorter.domain.Rack;
import com.marcel.sorter.domain.Sample;
import com.marcel.sorter.repository.RackRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
@AllArgsConstructor
public class SorterService {

    private final RackRepository rackRepository;

    @Transactional
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttemptsExpression = "#{${sorter.retry.maxAttempts}}",
            backoff = @Backoff(delayExpression = "#{${sorter.retry.backoffDelayInMillis}}"))
    public Assignment assign(Sample sample) throws NoRackAvailableException {
        log.trace("Started assignment for {}", sample);
        List<Long> rackIdsList = rackRepository.findRacksNotHavingAgeCompanyDistrictAndVisionDefect(sample.getPatientAge(), sample.getPatientCompany(),
                sample.getPatientCityDistrict(), sample.getPatientVisionDefect());
        if (CollectionUtils.isEmpty(rackIdsList)) {
            log.warn("No rack available for sample {}", sample);
            throw new NoRackAvailableException("Could not find any available rack for given params");
        }
        log.trace("Found rack ids available for sample {}: {} ", sample, rackIdsList);
        Long rackId = rackIdsList.get(new Random().nextInt(rackIdsList.size()));
        log.debug("Try to assign sample {} to rack ID {}", sample, rackId);
        Rack rack = this.rackRepository.findWithLockingById(rackId).orElseThrow(() -> new IllegalStateException("Could not load rack with id"+rackId));
        validateRackWithSample(sample, rack);
        log.info("Assigned rack id {} for sample {}", rack.getId(), sample);
        rack.addSample(sample);
        Rack savedRack = rackRepository.save(rack);
        Assignment sa = Assignment.of(savedRack.getSamples().get(savedRack.getSamples().size() - 1).getId(), rackId);
        log.debug("Attempt to save assignment {}", sa);
        return sa;
    }

    private static void validateRackWithSample(Sample sample, Rack rack) {
        if (rack.contains(sample)) {
            log.trace("Rack already changed and contains same data");
            throw new ObjectOptimisticLockingFailureException(Rack.class, rack);
        }
        if (rack.getSamples().size() == rack.getCapacity()) {
            log.trace("Rack already changed and exceeded capacity");
            throw new ObjectOptimisticLockingFailureException(Rack.class, rack);
        }
    }
}
