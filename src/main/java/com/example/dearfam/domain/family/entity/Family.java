package com.example.dearfam.domain.family.entity;

import com.example.dearfam.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "family")
public class Family extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "family_id")
    private Long id;

    @Column(name = "family_name", nullable = false, length = 20)
    private String familyName;

    @Column(name = "parent_count")
    private Integer parentCount;

    @Column(name = "child_count")
    private Integer childCount;

    @Builder
    public Family(Long id, String familyName, Integer parentCount, Integer childCount) {
        this.id = id;
        this.familyName = familyName;
        this.parentCount = parentCount != null ? parentCount : 0;
        this.childCount = childCount != null ? childCount : 0;
    }

}
