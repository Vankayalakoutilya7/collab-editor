package com.koutilya.collabeditor.service;

import com.koutilya.collabeditor.model.Document;
import com.koutilya.collabeditor.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository repository;

    public String getContent(String docId) {
        return repository.findByDocId(docId)
                .map(Document::getContent)
                .orElse("");
    }

    public void saveContent(String docId, String content) {
        Document doc = repository.findByDocId(docId)
                .orElse(new Document());

        doc.setDocId(docId);
        doc.setContent(content);
        doc.setUpdatedAt(Instant.now());
        repository.save(doc);
    }
}