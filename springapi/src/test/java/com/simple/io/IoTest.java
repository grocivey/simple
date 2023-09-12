package com.simple.io;

import com.simple.api.util.GsonUtil;
import org.springframework.util.StopWatch;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.IntStream;

public class IoTest {
    public static void main(String[] args) throws IOException {
        String cmd = "{\n" +
                "\t\"PK_Type\":\t{\n" +
                "\t\t\"Name\":\t\"GET_DEVICES_ACK\",\n" +
                "\t\t\"Code\":\t\"3302\"\n" +
                "\t},\n" +
                "\t\"Info\":\t{\n" +
                "\t\t\"Result\":\t\"1\",\n" +
                "\t\t\"ReqNo\":\t\"1\",\n" +
                "\t\t\"TimeStamp\":\t\"2023-8-31 10:1:43\",\n" +
                "\t\t\"FSUID\":\t\"44098343800536\",\n" +
                "\t\t\"FSUCode\":\t\"44098343800536\",\n" +
                "\t\t\"TaskID\":\t\"\",\n" +
                "\t\t\"FSUType\":\t\"DTM-TY-1000E2\",\n" +
                "\t\t\"FSUManufactor\":\t\"DTYD\",\n" +
                "\t\t\"Count\":\t6,\n" +
                "\t\t\"Devices\":\t[{\n" +
                "\t\t\t\t\"DeviceName\":\t\"开关电源01\",\n" +
                "\t\t\t\t\"DeviceId\":\t44098340600559,\n" +
                "\t\t\t\t\"DeviceType\":\t\"6\",\n" +
                "\t\t\t\t\"PortType\":\t\"Uart\",\n" +
                "\t\t\t\t\"Model\":\t\"cnTower-Module-Power-v2.0\",\n" +
                "\t\t\t\t\"Version\":\t\"2.0.0\",\n" +
                "\t\t\t\t\"Desc\":\t\"\",\n" +
                "\t\t\t\t\"ProtoFile\":\t\"lib_406_ZGTT_QZTT2229-2021.lua\",\n" +
                "\t\t\t\t\"ProtoFileHash\":\t\"9f450ff097b9ee6b2e0a9c0f02ddc57f\",\n" +
                "\t\t\t\t\"Status\":\t0,\n" +
                "\t\t\t\t\"Intelligent\":\t{\n" +
                "\t\t\t\t\t\"ResName\":\t\"Local/Uart/3\",\n" +
                "\t\t\t\t\t\"Address\":\t1,\n" +
                "\t\t\t\t\t\"BaudRate\":\t9600,\n" +
                "\t\t\t\t\t\"DataSize\":\t8,\n" +
                "\t\t\t\t\t\"StopBit\":\t1,\n" +
                "\t\t\t\t\t\"Parity\":\t0\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"Unintelligent\":\t[]\n" +
                "\t\t\t}, {\n" +
                "\t\t\t\t\"DeviceName\":\t\"蓄电池01\",\n" +
                "\t\t\t\t\"DeviceId\":\t44098340700735,\n" +
                "\t\t\t\t\"DeviceType\":\t\"7\",\n" +
                "\t\t\t\t\"PortType\":\t\"AI\",\n" +
                "\t\t\t\t\"Model\":\t\"ai-bat-current\",\n" +
                "\t\t\t\t\"Version\":\t\"1.0.0\",\n" +
                "\t\t\t\t\"Desc\":\t\"\",\n" +
                "\t\t\t\t\"ProtoFile\":\t\"libyytd_ai_bat\",\n" +
                "\t\t\t\t\"ProtoFileHash\":\t\"5f0b1e9df4aba67edb1a1904751b7960\",\n" +
                "\t\t\t\t\"Status\":\t0,\n" +
                "\t\t\t\t\"Intelligent\":\t{\n" +
                "\t\t\t},\n" +
                "\t\t\t\t\"Unintelligent\":\t[{\n" +
                "\t\t\t\t\t\t\"ResName\":\t\"Local/AI/5\",\n" +
                "\t\t\t\t\t\t\"Type\":\t1,\n" +
                "\t\t\t\t\t\t\"SigId\":\t407106001,\n" +
                "\t\t\t\t\t\t\"SigName\":\t\"前半组电压\",\n" +
                "\t\t\t\t\t\t\"PortType\":\t\"AI\"\n" +
                "\t\t\t\t\t}, {\n" +
                "\t\t\t\t\t\t\"ResName\":\t\"Local/AI/6\",\n" +
                "\t\t\t\t\t\t\"Type\":\t1,\n" +
                "\t\t\t\t\t\t\"SigId\":\t407107001,\n" +
                "\t\t\t\t\t\t\"SigName\":\t\"后半组电压\",\n" +
                "\t\t\t\t\t\t\"PortType\":\t\"AI\"\n" +
                "\t\t\t\t\t}]\n" +
                "\t\t\t}, {\n" +
                "\t\t\t\t\"DeviceName\":\t\"烟感01\",\n" +
                "\t\t\t\t\"DeviceId\":\t44098341820574,\n" +
                "\t\t\t\t\"DeviceType\":\t\"182\",\n" +
                "\t\t\t\t\"PortType\":\t\"DI\",\n" +
                "\t\t\t\t\"Model\":\t\"smoke_ensor\",\n" +
                "\t\t\t\t\"Version\":\t\"1.0.0\",\n" +
                "\t\t\t\t\"Desc\":\t\"\",\n" +
                "\t\t\t\t\"ProtoFile\":\t\"libyytd_smoke_sensor\",\n" +
                "\t\t\t\t\"ProtoFileHash\":\t\"c33fc5f7cd381ee9b436718d1b937098\",\n" +
                "\t\t\t\t\"Status\":\t0,\n" +
                "\t\t\t\t\"Intelligent\":\t{\n" +
                "\t\t\t},\n" +
                "\t\t\t\t\"Unintelligent\":\t[{\n" +
                "\t\t\t\t\t\t\"ResName\":\t\"Local/DI/1\",\n" +
                "\t\t\t\t\t\t\"Type\":\t0,\n" +
                "\t\t\t\t\t\t\"SigId\":\t418002001,\n" +
                "\t\t\t\t\t\t\"SigName\":\t\"烟雾告警\",\n" +
                "\t\t\t\t\t\t\"PortType\":\t\"DI\"\n" +
                "\t\t\t\t\t}]\n" +
                "\t\t\t}, {\n" +
                "\t\t\t\t\"DeviceName\":\t\"温湿度01\",\n" +
                "\t\t\t\t\"DeviceId\":\t44098341830645,\n" +
                "\t\t\t\t\"DeviceType\":\t\"183\",\n" +
                "\t\t\t\t\"PortType\":\t\"AI\",\n" +
                "\t\t\t\t\"Model\":\t\"humiture-current_2\",\n" +
                "\t\t\t\t\"Version\":\t\"1.0.0\",\n" +
                "\t\t\t\t\"Desc\":\t\"\",\n" +
                "\t\t\t\t\"ProtoFile\":\t\"libyytd_humiture_current1-DT1\",\n" +
                "\t\t\t\t\"ProtoFileHash\":\t\"200d55c59092cdeb37bb473cc037d212\",\n" +
                "\t\t\t\t\"Status\":\t0,\n" +
                "\t\t\t\t\"Intelligent\":\t{\n" +
                "\t\t\t},\n" +
                "\t\t\t\t\"Unintelligent\":\t[{\n" +
                "\t\t\t\t\t\t\"ResName\":\t\"Local/AI/1\",\n" +
                "\t\t\t\t\t\t\"Type\":\t1,\n" +
                "\t\t\t\t\t\t\"SigId\":\t418101001,\n" +
                "\t\t\t\t\t\t\"SigName\":\t\"温度\",\n" +
                "\t\t\t\t\t\t\"PortType\":\t\"AI\"\n" +
                "\t\t\t\t\t}, {\n" +
                "\t\t\t\t\t\t\"ResName\":\t\"Local/AI/2\",\n" +
                "\t\t\t\t\t\t\"Type\":\t1,\n" +
                "\t\t\t\t\t\t\"SigId\":\t418102001,\n" +
                "\t\t\t\t\t\t\"SigName\":\t\"湿度\",\n" +
                "\t\t\t\t\t\t\"PortType\":\t\"AI\"\n" +
                "\t\t\t\t\t}]\n" +
                "\t\t\t}, {\n" +
                "\t\t\t\t\"DeviceName\":\t\"门磁01\",\n" +
                "\t\t\t\t\"DeviceId\":\t44098341850199,\n" +
                "\t\t\t\t\"DeviceType\":\t\"185\",\n" +
                "\t\t\t\t\"PortType\":\t\"DI\",\n" +
                "\t\t\t\t\"Model\":\t\"gate_magnetism\",\n" +
                "\t\t\t\t\"Version\":\t\"1.0.0\",\n" +
                "\t\t\t\t\"Desc\":\t\"\",\n" +
                "\t\t\t\t\"ProtoFile\":\t\"librtfh_gate_magnetism-DT1\",\n" +
                "\t\t\t\t\"ProtoFileHash\":\t\"b50e0d501610bdb50b6ea30f2fde840a\",\n" +
                "\t\t\t\t\"Status\":\t0,\n" +
                "\t\t\t\t\"Intelligent\":\t{\n" +
                "\t\t\t},\n" +
                "\t\t\t\t\"Unintelligent\":\t[{\n" +
                "\t\t\t\t\t\t\"ResName\":\t\"Local/DI/7\",\n" +
                "\t\t\t\t\t\t\"Type\":\t0,\n" +
                "\t\t\t\t\t\t\"SigId\":\t418016001,\n" +
                "\t\t\t\t\t\t\"SigName\":\t\"门磁开关状态\",\n" +
                "\t\t\t\t\t\t\"PortType\":\t\"DI\"\n" +
                "\t\t\t\t\t}]\n" +
                "\t\t\t}, {\n" +
                "\t\t\t\t\"DeviceName\":\t\"智能动环监控设备\",\n" +
                "\t\t\t\t\"DeviceId\":\t44098343800536,\n" +
                "\t\t\t\t\"DeviceType\":\t\"38\",\n" +
                "\t\t\t\t\"PortType\":\t\"\",\n" +
                "\t\t\t\t\"Model\":\t\"\",\n" +
                "\t\t\t\t\"Version\":\t\"\",\n" +
                "\t\t\t\t\"Desc\":\t\"\",\n" +
                "\t\t\t\t\"ProtoFile\":\t\"\",\n" +
                "\t\t\t\t\"ProtoFileHash\":\t\"00000000000000000000000000000000\",\n" +
                "\t\t\t\t\"Status\":\t0,\n" +
                "\t\t\t\t\"Intelligent\":\t{\n" +
                "\t\t\t},\n" +
                "\t\t\t\t\"Unintelligent\":\t[]\n" +
                "\t\t\t}]\n" +
                "\t}\n" +
                "}";

        String json = GsonUtil.toJson(GsonUtil.fromJson(cmd,Object.class));
        String consumer="2023-08-31 10:01:49.148 [pool-2-thread-12] [INFO ] c.c.product.fsumqproxy.mqtt.MqttClientCallback:77 - 消费--MQTTServerURI【tcp://113.108.164.251:1883】,MQTTTopic【fsu/DT/44098343800536/result】,Message【"+json+"】\n";
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("files");
        IntStream.rangeClosed(1,50000).forEach(__->{
            try {
                Files.write(Paths.get("D:/fsudemo5.log"),consumer.getBytes(Charset.forName("GBK")),StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        stopWatch.stop();
        stopWatch.start("stream");
        try(OutputStream outputStream = Files.newOutputStream(Paths.get("D:/fsudemo6.log"),StandardOpenOption.CREATE, StandardOpenOption.APPEND);){
            IntStream.rangeClosed(1,50000).forEach(__->{
                try {
                    outputStream.write(consumer.getBytes(Charset.forName("GBK")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            outputStream.flush();
        }
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());

        FileOutputStream outputStream = new FileOutputStream("D:/fsudemo6.log");
    }
}
