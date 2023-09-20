# history修改时间格式

### 临时修改

在命令行中执行以下命令即可
```shell
export HISTTIMEFORMAT="%F %T  `whoami`  "
```
### 永久修改

在`/etc/profile` 或 `~/.bash_profile` 或 `~/.bashrc`中加入以下内容
```shell
export HISTTIMEFORMAT="%F %T  `whoami`  "
```

**`/etc/profile` 、 `~/.bash_profile` 、 `~/.bashrc` 三者区别如**
* `/etc/profile` 为系统所有用户生效
* `~/.bash_profile` 用户每次登录执行一次
* `~/.bashrc` 打开新的终端执行一次
