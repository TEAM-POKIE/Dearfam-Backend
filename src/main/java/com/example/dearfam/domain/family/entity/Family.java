package com.example.dearfam.domain.family.entity;

import com.example.dearfam.common.entity.BaseTimeEntity;
import com.example.dearfam.domain.animatedphoto.entity.AnimatePhoto;
import com.example.dearfam.domain.diary.entity.DiaryBook;
import com.example.dearfam.domain.memoryposts.memorypost.entity.MemoryPost;
import com.example.dearfam.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


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

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Users> users = new ArrayList<>();

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemoryPost> memoryPosts = new ArrayList<>();

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryBook> diaryBooks = new ArrayList<>();

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnimatePhoto> animatePhotos = new ArrayList<>();

    @Builder
    public Family(Long id, String familyName, Integer parentCount, Integer childCount) {
        this.id = id;
        this.familyName = familyName;
        this.parentCount = parentCount != null ? parentCount : 0;
        this.childCount = childCount != null ? childCount : 0;
    }

}
