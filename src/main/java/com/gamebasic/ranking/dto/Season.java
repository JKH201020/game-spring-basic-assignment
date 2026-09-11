package com.gamebasic.ranking.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class Season {

    private final String id;
    private final String name;
    private final LocalDateTime startsAt;
    private final LocalDateTime endsAt;
}
