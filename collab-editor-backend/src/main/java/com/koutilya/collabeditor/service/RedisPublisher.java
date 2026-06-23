package com.koutilya.collabeditor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class RedisPublisher {

    public static final String CHANNEL = "document-updates";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void publish(String docId, byte[] message) {
        String payload = docId + "|" + Arrays.toString(message);

        System.out.println("Publishing to Redis...");

        redisTemplate.convertAndSend(CHANNEL, payload);
    }
}