package com.koutilya.collabeditor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
public class RedisSubscriber {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void handleMessage(byte[] message) {

        String payload = new String(message);

        String[] parts = payload.split("\\|", 2);
        String docId = parts[0];
        String data = parts[1];

        System.out.println("🔥 Redis received doc: " + docId);

        messagingTemplate.convertAndSend(
                "/topic/document/" + docId,
                data
        );
    }
}