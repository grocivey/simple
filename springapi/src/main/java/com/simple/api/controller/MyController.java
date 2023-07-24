package com.simple.api.controller;

import com.simple.api.entity.RemoteUpgradeResponse;
import com.simple.api.service.BaseStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/function/facility/remote")
public class MyController {

    @Autowired
    BaseStatService baseStatService;

    /**
     *
     * @return
     */
    @PostMapping("/upgrade")
    public RemoteUpgradeResponse  upgrade(){
        RemoteUpgradeResponse response = new RemoteUpgradeResponse();
        RemoteUpgradeResponse.BatchId batchId = new RemoteUpgradeResponse.BatchId();
        batchId.setBatchId("bi1");
        response.setCode(201);
        response.setMsg("ok");
        response.setData(batchId);
        return response;
    }


    @GetMapping("/getUserCount")
    public Object  getUserCount(){
//        return baseStatService.list();
        return baseStatService.getById("32dde7a2-eec9-11ed-961d-8c554a55b369");
    }

}
