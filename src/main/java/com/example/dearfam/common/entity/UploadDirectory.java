package com.example.dearfam.common.entity;

import lombok.Getter;

@Getter
public enum UploadDirectory {
    POSTS("posts"),
    PROFILES("profiles");

    private final String baseDir;

    UploadDirectory(String baseDir) {
        this.baseDir = baseDir;
    }

}
