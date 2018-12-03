package com.watent.dr;

import com.watent.dr.service.TxService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JtaApplication.class)
@EnableTransactionManagement
public class JtaApplicationTests {

    @Autowired
    TxService txService;

    @Test
    public void testTransaction() {
        try {
            txService.orderSend();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
