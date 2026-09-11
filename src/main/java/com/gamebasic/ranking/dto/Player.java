package com.gamebasic.ranking.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Player {

    private final String id;
    private final String name;
    private final String region;
    private final List<String> tags;
}
