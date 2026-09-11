package com.gamebasic.ranking.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class RankingResponse {

    private final String season;
    private final int totalRecords;
    private final int excludedCount;
    private final List<RankingEntriesResponse> entries;
}
