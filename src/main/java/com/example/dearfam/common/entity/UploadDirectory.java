package com.example.dearfam.common.entity;

import lombok.Getter;

@Getter
public enum UploadDirectory {
    POSTS("posts"),
    PROFILES("profiles"),
    DIARY("diary");

    private final String baseDir;

    UploadDirectory(String baseDir) {
        this.baseDir = baseDir;
    }

}
