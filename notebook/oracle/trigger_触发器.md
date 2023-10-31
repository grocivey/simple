# 触发器（oracle ID 实现自增）

**删除触发器**
```oracle
DROP TRIGGER DATA_DICTIONARY_ITEM_TRIGGER;
```

**删除sequence**
```oracle
DROP SEQUENCE DATA_DICTIONARY_ITEM_SEQUENCE;
```

**创建sequence**

`INCREMENT BY 1 `   表示step增加1
` START WITH 882`   表示从882开始
```oracle
CREATE SEQUENCE DATA_DICTIONARY_ITEM_SEQUENCE INCREMENT BY 1 START WITH 882 NOMAXVALUE NOCYCLE NOCACHE;
```


**创建触发器**
```oracle
CREATE TRIGGER "DATA_DICTIONARY_ITEM_TRIGGER" BEFORE INSERT ON "T_FSU_DATA_DICTIONARY_ITEM" REFERENCING OLD AS "OLD" NEW AS "NEW" FOR EACH ROW
begin
    select DATA_DICTIONARY_ITEM_SEQUENCE.nextval into :new.id from dual;
end;
```

**查询触发器或sequence**
```oracle
select * from ALL_TRIGGER_COLS where TRIGGER_OWNER = 'WSFSU' and TABLE_NAME = 'T_FSU_DATA_DICTIONARY';
select * from ALL_SEQUENCES where SEQUENCE_OWNER = 'WSFSU' and SEQUENCE_NAME like  '%DIC%';
```


***注意：如果要修改sequence的值可以先drop然后重新创建***

