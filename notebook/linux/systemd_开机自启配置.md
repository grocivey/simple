# systemd开机自启配置

**第一步 创建service**

在`/etc/systemd/system`或`/usr/lib/systemd/system`下创建`emqx.service`文件,内容如下，

```
[Unit]
Description=Emqx daemon
After=network.target

[Service]
Type=forking
ExecStart=/app/inspur/emqx/bin/emqx start
User=root
WorkingDirectory=/app/inspur/emqx/bin

[Install]
WantedBy=multi-user.target
```

**第二步 重新加载Systemd**
```bash
systemctl daemon-reload
```

**第三步 开机自启(开启单元)**
```bash
systemctl enable emqx.service
```

* 注意点
  * `ExecStart=/app/inspur/emqx/bin/emqx start`中不可使用重定向，比如 `>>` `> `,且命令需要绝对路径, 如需使用重定向操作符可以自建执行脚本，比如
    ```
    #!/bin/bash
    echo 123 >> /path/to/xxx.txt
    ```

**参考  https://blog.csdn.net/qq_41084756/article/details/130200523**  
