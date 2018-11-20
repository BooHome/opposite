package club.ihere.wechat.controller.wechat;

import club.ihere.wechat.common.json.JsonResult;
import club.ihere.wechat.common.json.JsonResultBuilder;
import club.ihere.wechat.service.wechat.WechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: fengshibo
 * @date: 2018/11/20 18:32
 * @description:
 */
@RestController
@RequestMapping("wechat/query")
public class WechatQueryController {

    private static Logger logger = LoggerFactory.getLogger(WechatQueryController.class);

    @Autowired
    private WechatService wechatService;

    @GetMapping("getUserAsMessage")
    public JsonResult< List<String>> getUserAsMessage(){
        List<String> result=wechatService.getUserAsMessage();
        return JsonResultBuilder.build(result);
    }


}
