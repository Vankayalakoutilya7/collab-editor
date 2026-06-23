package com.koutilya.collabeditor.model;

import lombok.Data;

@Data
public class DocumentUpdate {
    private String documentId;
    private String content;
}