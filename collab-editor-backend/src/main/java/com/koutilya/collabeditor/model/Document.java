package com.koutilya.collabeditor.model;

import org.springframework.data.annotation.Id;
import java.time.Instant;

@org.springframework.data.mongodb.core.mapping.Document(collection = "documents")
public class Document {

    @Id
    private String id;

    private String docId;

    private String content;

    private Instant updatedAt;

    public Document() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDocId() { return docId; }
    public void setDocId(String docId) { this.docId = docId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}