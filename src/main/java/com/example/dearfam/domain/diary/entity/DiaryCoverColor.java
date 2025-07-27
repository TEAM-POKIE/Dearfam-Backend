package com.example.dearfam.domain.diary.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiaryCoverColor {
    // 디자인에서 구성한 색상들 설정

    GRAY("#d0d5d7"),
    BLUE("#95b2d5"),
    MINT("#9ed1d3"),
    SKY("#cbdfe3"),
    BEIGE("#f0e7bf"),
    SAND("#cdba8e"),
    ROSE("#b58094"),
    LAVENDER("#e1e4f3"),
    CREAM("#f1f0d1"),
    GREEN("#d4dfa6");

    private final String hex;

}
