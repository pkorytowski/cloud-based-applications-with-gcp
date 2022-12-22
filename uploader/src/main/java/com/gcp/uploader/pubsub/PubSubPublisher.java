package com.gcp.uploader.pubsub;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
public abstract class PubSubPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(PubSubPublisher.class);

    @Autowired
    private PubSubTemplate pubSubTemplate;

    protected abstract String topic();

    public void publish(PubsubMessage pubsubMessage) throws ExecutionException, InterruptedException {
        LOG.info("Publishing to the topic [{}], message [{}]", topic(), pubsubMessage);
        pubSubTemplate.publish(topic(), pubsubMessage).get();
    }

    public void publishMessage(Map<String, String> attributeMap) throws ExecutionException, InterruptedException {
        LOG.info("Sending Message to the topic:::");
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .putAllAttributes(attributeMap)
                //.setData(ByteString.copyFromUtf8(message))
                //.setMessageId(messageId)
                .build();

        publish(pubsubMessage);
    }

}
