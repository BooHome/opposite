package club.ihere.wechat.main;

import club.ihere.wechat.common.config.ConstantConfig;
import club.ihere.wechat.service.wechat.WechatService;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedHashMap;
import java.util.List;
@RunWith(SpringRunner.class)
@SpringBootTest
public class DoTest {

    @Autowired
    private WechatService wechatService;

    @Test
    public void test() {
        List<String> userList = wechatService.getUserList();
        System.out.println(userList.size());
    }
}
