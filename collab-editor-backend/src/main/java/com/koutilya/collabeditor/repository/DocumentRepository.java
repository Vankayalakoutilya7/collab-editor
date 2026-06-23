package com.koutilya.collabeditor.repository;

import com.koutilya.collabeditor.model.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface DocumentRepository extends MongoRepository<Document, String> {

    // Find a document by its human-readable ID (e.g. "project-notes")
    Optional<Document> findByDocId(String docId);
}