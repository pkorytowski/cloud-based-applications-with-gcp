package com.gcp.uploader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UploaderPublisher extends PubSubPublisher{

    @Value("${pubsub.topic}")
    private String topic;

    @Override
    protected String topic() {
        return topic;
    }
}
