package com.gamebasic.ranking.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class Meta {

    private final Season season;
    private final LocalDateTime generatedAt;
    private final int schemaVersion;
    private final int totalRecords;
}
