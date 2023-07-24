package com.simple.api.mqtt;


import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class MqttClientConnect {

    private MqttClient mqttClient;

    public MqttClient getMqttClient() {
        return mqttClient;
    }

    /**
     * 系统的mqtt客户端id
     */
    private String mqttClientId;

    /**
     * 客户端
     */
    public static ConcurrentHashMap<String, MqttClientConnect> mqttClients = new ConcurrentHashMap();

    //用来发送的客户端
    public static ConcurrentMap<String, MqttClientConnect> pubMqttClients = new ConcurrentHashMap<>();

    /**
     * 订阅的topic
     */
    public static ConcurrentHashMap<String, String> topics = new ConcurrentHashMap();

    /**
     * 客户端connect连接mqtt服务器
     *
     * @param userName     用户名
     * @param passWord     密码
     * @param mqttCallback 回调函数
     **/
    public void setMqttClient(String host, String clientId, String userName, String passWord, boolean cleanSession,  MqttClientCallback mqttCallback) throws MqttException {
        MqttClientPersistence persistence= new MqttDefaultFilePersistence();
        MqttConnectOptions options = mqttConnectOptions(host, clientId, userName, passWord, cleanSession, persistence);
        if (mqttCallback == null) {
            mqttClient.setCallback(new MqttClientCallback(mqttClientId).setPersistence(persistence).setClient(mqttClient));
        } else {
            mqttClient.setCallback(mqttCallback.setPersistence(persistence).setClient(mqttClient));
        }
        //手动提交
        mqttClient.setManualAcks(true);
        mqttClient.connect(options);
    }

    /**
     * MQTT连接参数设置
     */
    private MqttConnectOptions mqttConnectOptions(String host, String clientId, String userName, String passWord, boolean cleanSession, MqttClientPersistence persistence) throws MqttException {
        //消息采用文件持久化
        mqttClient = new MqttClient(host, clientId, persistence);
        MqttConnectOptions options = new MqttConnectOptions();
        if (userName != null && passWord != null){
            options.setUserName(userName);
            options.setPassword(passWord.toCharArray());
        }
        options.setConnectionTimeout(1000);///默认：30
        options.setAutomaticReconnect(true);//默认：false
        options.setCleanSession(cleanSession);//默认：true
        options.setKeepAliveInterval(60);//默认：60
        options.setMaxInflight(20000);
        options.setAutomaticReconnect(true);
        return options;
    }

    /**
     * 关闭MQTT连接
     */
    public void close() throws MqttException {
        mqttClient.disconnect();
        mqttClient.close();
    }

    /**
     * 向某个主题发布消息 默认qos：1
     *
     * @param topic:发布的主题
     * @param msg：发布的消息
     */
    public void pub(String topic, String msg) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(msg.getBytes());
        MqttTopic mqttTopic = mqttClient.getTopic(topic);
        MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
        token.waitForCompletion();
    }

    /**
     * 向某个主题发布消息
     *
     * @param topic: 发布的主题
     * @param msg:   发布的消息
     * @param qos:   消息质量    Qos：0、1、2
     */
    public void pub(String topic, String msg, int qos) throws MqttException, UnsupportedEncodingException {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(qos);

        mqttMessage.setPayload(msg.getBytes("UTF-8"));

        // mqttMessage.setPayload(msg.getBytes());
        MqttTopic mqttTopic = mqttClient.getTopic(topic);
        MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
        token.waitForCompletion();
        log.info("发布--MQTTServerURI【{}】,MQTTTopic【{}】,Message【{}】",this.mqttClient.getServerURI(),topic,msg);
    }

    /**
     * 订阅多个主题 ，此方法默认的的Qos等级为：1
     *
     * @param topic 主题
     */
    public void sub(String[] topic) throws MqttException {
        mqttClient.subscribe(topic);
    }

    /**
     * 订阅某一个主题，可携带Qos
     *
     * @param topic 所要订阅的主题
     * @param qos   消息质量：0、1、2
     */
    public void sub(String topic, int qos) throws MqttException {
        mqttClient.subscribe(topic, qos);
    }

    public String getMqttClientId() {
        return mqttClientId;
    }

    public void setMqttClientId(String mqttClientId) {
        this.mqttClientId = mqttClientId;
    }

}
