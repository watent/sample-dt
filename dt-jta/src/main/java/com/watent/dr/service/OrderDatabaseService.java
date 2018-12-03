package com.watent.dr.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * OrderDatabaseService
 * 下单服务
 *
 * @author Dylan
 */
@Service
public class OrderDatabaseService {

    @Resource(name = "orderJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    /**
     * 1.保存订单记录
     * <p>
     * userId				用户ID
     * orderId				订单编号
     * orderContent		订单内容
     *
     * @throws Exception 抛个异常
     */
    public void saveOrder(JSONObject orderInfo) throws Exception {

        String sql = "insert into table_order (order_id, user_id, order_content, create_time) values (?, ?, ?,now())";
        // 1. 添加订单记录
        int count = jdbcTemplate.update(sql, orderInfo.get("orderId"), orderInfo.get("userId"), orderInfo.get("orderContent"));

        if (count != 1) {
            throw new Exception("订单创建失败，原因[数据库操作失败]");
        }
    }
}
