# 如何使用copy命令从其他数据库导入到当前数据库

## 第一步 进入数据库A控制台



```shell
sqlplus 账号/密码@IP:端口/服务名
```

## 第二步 从B库到入到A库

其中`create`可以换成`insert`,`append`,create表示新建表，其他两个顾名思义
默认arraysize是15，可以使用 set arrarsize xxx来设置，可以通过 show arraysize查看

```oracle
copy from 账号/密码@IP:端口/服务名 create A.表 using select * from B.表 where rownum < 50001;
```

