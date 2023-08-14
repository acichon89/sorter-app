package com.marcel.sorter.service;

import lombok.Value;

@Value(staticConstructor = "of")
public class Assignment {
    private Long sampleId;
    private Long rackId;

}
