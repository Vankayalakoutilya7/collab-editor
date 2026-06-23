package com.koutilya.collabeditor.controller;

import com.koutilya.collabeditor.service.DocumentService;
import com.koutilya.collabeditor.service.RedisPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DocumentController {

    @Autowired
    private RedisPublisher redisPublisher;

    @Autowired
    private DocumentService documentService;

    // Receive Yjs binary update → publish to Redis → broadcast to all clients
    @MessageMapping("/edit/{docId}")
    public void handleEdit(@DestinationVariable String docId, List<Integer> update) {
        byte[] byteArray = new byte[update.size()];
        for (int i = 0; i < update.size(); i++) {
            byteArray[i] = update.get(i).byteValue();
        }
        redisPublisher.publish(docId, byteArray);
    }

    // Receive plain text snapshot → save to MongoDB
    @MessageMapping("/save/{docId}")
    public void handleSave(@DestinationVariable String docId, String content) {
        documentService.saveContent(docId, content);
    }

    // REST: load saved content when a user opens a document
    @GetMapping("/document/{docId}")
    @ResponseBody
    public ResponseEntity<String> getDocument(@PathVariable String docId) {
        String content = documentService.getContent(docId);
        return ResponseEntity.ok(content);
    }
}