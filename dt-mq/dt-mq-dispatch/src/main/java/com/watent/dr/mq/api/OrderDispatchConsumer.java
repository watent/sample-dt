package com.watent.dr.mq.api;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.watent.dr.mq.service.DispatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * 消费者，取调度队列
 *
 * @author Tony
 */
@Component
public class OrderDispatchConsumer {
    private final Logger logger = LoggerFactory.getLogger(OrderDispatchConsumer.class);

    @Autowired
    DispatchService dispatchService;

    @RabbitListener(queues = "orderDispatchQueue")
    public void messageConsumer(String message, Channel channel,
                                @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        try {
            // mq里面的数据转为json对象
            JSONObject orderInfo = JSONObject.parseObject(message);
            logger.warn("收到MQ里面的消息：" + orderInfo.toJSONString());
            Thread.sleep(5000L);

            // 执行业务操作。
            // 幂等性，不要重复性。 如果有重复了，根据业务去判断。 这里我是设置了订单号为主键
            String orderId = orderInfo.getString("orderId");
            dispatchService.dispatch(orderId);
            // ack - 告诉MQ，我已经收到啦
            channel.basicAck(tag, false);
        } catch (SQLException exception) {
            logger.error("消费出错重发", exception);
            logger.error("这个错误系统可以自己修复，通知MQ再重发试试");
            // 建议，都要记录一下重试次数
            // Nack - 告诉MQ，我收到了，但是有意料不到的异常，再给我发一次。
            // requeue: true是继续， false是丢弃或者丢到死信队列
            channel.basicNack(tag, false, true);
            // 根据不同的异常，和业务需要，采取不通的措施
        } catch (Exception e) {
            logger.error("出错，丢弃", e);
            logger.error("这个错误是开发人员也没意料到，所以不用重发，我通过监控系统通知人工处理", e);
            // Nack - 不可能天天有异常的
            channel.basicNack(tag, false, false);
        }
        // 如果不给回复，就等这个consumer断开链接后再继续

    }
}
