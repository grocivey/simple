package com.simple.api.mqtt;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.scheduling.annotation.Async;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 常规MQTT回调函数
 *
 */
@Slf4j
public class MqttClientCallback implements MqttCallbackExtended {

    private static final String PERSISTENCE_RECEIVED_PREFIX = "r-";

    private MqttClient client;

    private MqttClientPersistence persistence;

    private String subTopic;


    public static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(32, 32, 10, TimeUnit.SECONDS, new SynchronousQueue<>(true),(r, executor) -> {
        try {
            executor.getQueue().put(r);
        } catch (InterruptedException e) {
            log.error("MqttClientCallback的pool中断异常",e);
        }
    });

    /**
     * 系统的mqtt客户端id
     */
    private String mqttClientId;

    public MqttClientCallback(String mqttClientId) {
        this.mqttClientId = mqttClientId;
    }

    /**
     * MQTT 断开连接会执行此方法
     */
    @SneakyThrows
    @Override
    public void connectionLost(Throwable throwable) {
        log.error("mqttClientId:{},断开了MQTT连接 :{},",mqttClientId, throwable);

    }

    /**
     * publish发布成功后会执行到这里
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info("发布消息成功");
    }

    /**
     * subscribe订阅后得到的消息会执行到这里
     */
    @SneakyThrows
    @Override
    @Async
    public void messageArrived(String topic, MqttMessage message){
        //去重
        if (!this.persistence.containsKey(PERSISTENCE_RECEIVED_PREFIX + message.getId())){
            log.warn("重复消息id:{}",message.getId());
            return;
        }
        try {
            threadPool.execute(() -> {
                try {
                    String serverURI = client.getServerURI();
                    String payload  = new String(message.getPayload(), StandardCharsets.UTF_8);
                    log.info("消费--MQTTServerURI【{}】,MQTTTopic【{}】,Message【{}】",serverURI,topic,payload);
                } catch (Exception e) {
                    log.error("消费MQTT报文信息出错,ErrorMessage:",e);
                }
            });
        } finally {
            //手动提交
            this.client.messageArrivedComplete(message.getId(),message.getQos());
        }
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        log.info("连接建立成功");

    }

    public MqttClientCallback setPersistence(MqttClientPersistence persistence) {
        this.persistence = persistence;
        return this;
    }

    public MqttClientPersistence getPersistence() {
        return persistence;
    }

    public MqttClient getClient() {
        return client;
    }

    public MqttClientCallback setClient(MqttClient client) {
        this.client = client;
        return this;
    }

    public String getSubTopic() {
        return subTopic;
    }

    public MqttClientCallback setSubTopic(String subTopic) {
        this.subTopic = subTopic;
        return this;
    }


}
