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

};

server.listen(port, hostname, () => {
    console.log(`Server running at http://${hostname}:${port}/`);

    amqp.connect(connectionOptions).then(async (connection) => {
        console.log("连接成功")
        // 创建通道
        const channel = await connection.createChannel();

        // 声明队列
        const queueName = 'grotest1';
        await channel.assertQueue(queueName, { durable: true });
        // 消费消息
        channel.consume(queueName, (msg) => {
            const message = msg.content.toString();
            console.log(' Received message  --> :', message);
            // 手动确认消息已处理
            channel.ack(msg);
        }, { noAck: false });

        //发送消息
        let msg = '你好，世界！'
        // const options = {
        //     priority:0,
        //     delivery_mode:	2,
        //      headers:{'__TypeId__':'java.lang.String',
        // content_encoding:	'UTF-8',
        // content_type:	'application/json'}};
        channel.sendToQueue('grotext22', Buffer.from(msg));
        console.log("  Sent  --> %s", msg);


    }).catch((error) => {
        console.error('Failed to connect to RabbitMQ server:', error);
    });
});


