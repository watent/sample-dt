package com.watent.dr.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.SQLException;

/**
 * DispatchService
 * 分单服务
 *
 * @author Dylan
 */
@Service
public class DispatchService {

    @Resource(name = "dispatchJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    /**
     * 添加调度信息
     *
     * @param orderId 订单ID
     */
    @Transactional
    public void dispatch(String orderId) throws Exception {

        // 往数据库插入一条记录 调度系统数据库事务2
        String sql = "insert into table_dispatch (dispatch_seq, order_id,dispatch_content) values (UUID(), ?, ?)";
        int update = jdbcTemplate.update(sql, orderId, "派送此订单");
        if (update != 1) {
            throw new SQLException("调度数据插入失败，原因[数据库操作]");
        }
        // 手工抛了异常
        int i = 1 / 0;
    }


}
