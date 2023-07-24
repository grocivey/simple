package com.simple.api.mqtt;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 项目启动 监听主题
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "mqtt.enable",havingValue = "true")
public class MqttClientListener implements ApplicationListener<ContextRefreshedEvent> {

    private volatile AtomicBoolean isInit = new AtomicBoolean(false);
    @Value("${mqtt.clientId}")
    private String clientId;
    @Value("${mqtt.host}")
    private String host;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("开始订阅MQTT服务!!");
        //防止重复触发
        if (!isInit.compareAndSet(false, true)) {
            return;
        }
        try {

            //增加发送的客户端
            MqttClientConnect mqttClientConnectPub = new MqttClientConnect();
            mqttClientConnectPub.setMqttClientId(clientId);
            MqttClientCallback mqttClientCallbackPub = new MqttClientCallback(clientId);
            mqttClientConnectPub.setMqttClient(host, "sc-outbound-" + clientId + "-" + "shen", null, null, false, mqttClientCallbackPub);
            if (mqttClientConnectPub.getMqttClient().isConnected()) {
                MqttClientConnect.pubMqttClients.put(clientId, mqttClientConnectPub);
                log.info("【{}】服务器MQTT，发送客户端连接成功!!", host);
            }
        } catch (Exception e) {
            log.error("【{}】服务器MQTT连接失败!!", host, e);
        }
        log.info("MQTT发送客户端{}个连接服务器成功!!!", MqttClientConnect.pubMqttClients.size());
    }

}
