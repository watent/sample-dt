package com.watent.dr.mq.txservice;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * 这是一个发送MQ消息，修改消息表的地方
 *
 * @author Dylan
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MQService {

    private final Logger logger = LoggerFactory.getLogger(MQService.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void setup() {

        // 消息发送完毕后，则回调此方法 ack代表发送是否成功
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            // ack为true，代表MQ已经准确收到消息
            if (!ack) {
                return;
            }
            try {
                // 2. 修改本地消息表的状态为“已发送”。
                String sql = "update tb_distributed_message set msg_status=1 where unique_id=?";
                int count = jdbcTemplate.update(sql, correlationData.getId());

                if (count != 1) {
                    logger.warn("警告：本地消息表的状态修改不成功");
                }
            } catch (Exception e) {
                logger.warn("警告：修改本地消息表的状态时出现异常", e);
            }
        });
    }

    /**
     * 发送MQ消息，修改本地消息表的状态
     *
     * @throws Exception e
     */
    public void sendMsg(JSONObject orderInfo) {
        try {
            // 1. 发送消息到MQ
            // CorrelationData 当收到消息回执时，会附带上这个参数
            rabbitTemplate.convertAndSend("createOrderExchange", "", orderInfo.toJSONString(),
                    new CorrelationData(orderInfo.getString("orderId")));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
