const http = require('http');

const hostname = '127.0.0.1';
const port = 3000;

const server = http.createServer((req, res) => {
    res.statusCode = 200;
    res.setHeader('Content-Type', 'text/plain');
    res.end('Hello World');
});


// 引入 RabbitMQ 客户端库
const amqp = require('amqplib');

// 连接 RabbitMQ 服务器
const connectionOptions = {
    protocol: 'amqp',
    hostname: '47.119.173.233',
    port: 5672, // 指定端口号
    username: 'admin',
    password: 'rh@2023', // 指定密码
};

server.listen(port, hostname, () => {
    console.log(`Server running at http://${hostname}:${port}/`);

    amqp.connect(connectionOptions).then(async (connection) => {
        console.log("连接成功")
        // 创建通道
        const channel = await connection.createChannel();

        // 声明队列
        // const queueName = 'grotest1';
        // await channel.assertQueue(queueName, { durable: true });
        // 声明随机队列
        const { queueName } = await channel.assertQueue('');
        // 将队列绑定到交换器
        await channel.bindQueue(queueName, "fanout.faye_wang_tofront", '');
        // 消费消息
        channel.consume(queueName, (msg) => {
            const message = msg.content.toString();
            console.log(' Received message  --> :'+queueName, message);
            // 手动确认消息已处理
            channel.ack(msg);
        }, { noAck: false });


    }).catch((error) => {
        console.error('Failed to connect to RabbitMQ server:', error);
    });
});


