package com.koutilya.collabeditor.controller;

import com.koutilya.collabeditor.model.DocumentUpdate;
import com.koutilya.collabeditor.service.RedisPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class DocumentController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // In-memory storage (temporary)
    private Map<String, String> documentStore = new ConcurrentHashMap<>();
        @Autowired
        private RedisPublisher redisPublisher;


    @MessageMapping("/edit/{docId}")
    public void handleEdit(@DestinationVariable String docId, List<Integer> update) {

        byte[] byteArray = new byte[update.size()];
        for (int i = 0; i < update.size(); i++) {
            byteArray[i] = update.get(i).byteValue();
        }

        System.out.println("Doc: " + docId + " | update: " + byteArray.length);

        redisPublisher.publish(docId, byteArray);
    }
}