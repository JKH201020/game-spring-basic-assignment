package com.gamebasic.runcard.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CardResponse {
    // TODO (Lv 5): API 명세의 카드 응답 JSON에 맞게 필드를 만들고 생성자에서 채우세요.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    @NotBlank
    private final String cardType;
    @Min(0)
    @Max(10)
    private final int acquiredFloor;

    public CardResponse(Long id, String cardType, int acquiredFloor) {
        this.id = id;
        this.cardType = cardType;
        this.acquiredFloor = acquiredFloor;
    }
}
