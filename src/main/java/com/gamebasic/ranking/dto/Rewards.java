package com.gamebasic.ranking.dto;

import com.gamebasic.runcard.entity.CardType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.smartcardio.Card;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Rewards {

    private final List<CardType> offered;
    private final CardType picked;
}
