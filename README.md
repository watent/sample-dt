#Distributed Transaction 分布式事务


##dt-jta
基于数据库XA/JTA协议的方式

###1.引入依赖

      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jta-atomikos</artifactId>
      </dependency>

###2.配置数据源

    spring:
      jta:
        atomikos:
          datasource:
            order-datasource:
              xa-properties:
                url: jdbc:mysql://db.watent.net:3306/test-order-db?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false
                user: tony
                password: tony
              xa-data-source-class-name: com.mysql.jdbc.jdbc2.optional.MysqlXADataSource
            dispatch-datasource:
              xa-properties:
                url: jdbc:mysql://db.watent.net:3306/test-dispatch-db?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false
                user: tony
                password: tony
              xa-data-source-class-name: com.mysql.jdbc.jdbc2.optional.MysqlXADataSource



##dt-mq

*需要MQ支持事务

*业务操作和本地消息表操作在一个事务内
    
    1. 订单入库；
    2. 生成一条需要发送到MQ的消息，并且保存在数据库；
    3. 调用API传递消息到MQ；
    4. 收到MQ的正确回执后，将数据库中消息状态调整为已发送；
    5. 消费者收到MQ中指定的消息，开始处理；
    5.1 处理成功，主动通知MQ消息已正确处理，删除吧；
    5.2 处理失败，通知MQ消息需要重新处理，再推送一次吧；
    5.3 处理失败，消费者认为此类错误，不需要再次处理，通知MQ删除(DLQ)

