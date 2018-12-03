package com.watent.dr.mq.txservice;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * OrderService2
 * 消息生产者
 *
 * @author Dylan
 */
@Service
public class OrderService2 {

    private final Logger logger = LoggerFactory.getLogger(OrderService2.class);

    @Autowired
    OrderDatabaseService2 orderDatabaseService;

    @Autowired
    MQService mqService;

    /**
     * 创建订单
     *
     * @throws Exception
     */
    public void createOrder(String userId, String orderContent) throws Exception {

        // 订单号生成
        String orderId = UUID.randomUUID().toString();
        JSONObject orderInfo = new JSONObject();
        orderInfo.put("orderId", orderId);
        orderInfo.put("userId", userId);
        orderInfo.put("orderContent", orderContent);

        // 1. 数据库操作
        orderDatabaseService.saveOrder(orderInfo);

        // 数据库事务 和 MQ操作分离
        // 2. 发送 mq
        mqService.sendMsg(orderInfo);

        System.out.println("订单创建成功");
    }

    // 创建一个HTTP请求工具类
    public RestTemplate createRestTemplate() {

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        // 链接超时时间 > 3秒
        requestFactory.setConnectTimeout(3000);
        // 处理超时时间 > 2 秒
        requestFactory.setReadTimeout(2000);
        return new RestTemplate(requestFactory);
    }
}
