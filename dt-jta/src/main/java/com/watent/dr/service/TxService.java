package com.watent.dr.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * TxService
 * 事务操作服务
 *
 * @author Dylan
 */
@Service
public class TxService {

    @Autowired
    DispatchService dispatchService;

    @Autowired
    OrderDatabaseService orderDatabaseService;

    /**
     * 下单并派单
     *
     * @throws Exception e
     */
    @Transactional(rollbackFor = Exception.class)
    public void orderSend() throws Exception {

        // 订单信息生成
        String orderId = UUID.randomUUID().toString();
        JSONObject orderInfo = new JSONObject();
        orderInfo.put("orderId", orderId);
        orderInfo.put("userId", "testUser");
        orderInfo.put("orderContent", "买了根黄瓜");

        // 1. 保存订单记录
        orderDatabaseService.saveOrder(orderInfo);
        // 2. 往数据库插入一条记录 调度系统数据库事务2
        dispatchService.dispatch(orderId);
    }


}
