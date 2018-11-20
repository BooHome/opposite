package club.ihere.wechat.controller.wechat;

import club.ihere.common.api.OauthAPI;
import club.ihere.common.api.UserAPI;
import club.ihere.common.api.config.ApiConfig;
import club.ihere.common.api.response.GetUserInfoResponse;
import club.ihere.common.message.BaseMsg;
import club.ihere.common.message.TextMsg;
import club.ihere.common.message.req.BaseEvent;
import club.ihere.common.message.req.TextReqMsg;
import club.ihere.common.util.current.JsonUtil;
import club.ihere.controller.BaseWechatController;
import club.ihere.wechat.common.config.WeChatConfig;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: fengshibo
 * @date: 2018/11/19 17:32
 * @description:
 */
@RestController
@RequestMapping("wechat")
public class WechatController extends BaseWechatController {

    private static Logger logger = LoggerFactory.getLogger(WechatController.class);

    @Override
    protected String getToken() {
        return WeChatConfig.getToken();
    }


    @Override
    protected BaseMsg handleSubscribe(BaseEvent event) {
        String fromUserName = event.getFromUserName();
        logger.info(JsonUtil.toJson(event));
        return new TextMsg("感谢您的关注!");
    }

    @Override
    protected BaseMsg handleTextMsg(TextReqMsg msg) {
        String content = msg.getContent();
        logger.debug("用户发送到服务器的内容:{}", content);
        return new TextMsg("服务器回复用户消息!");
    }
}
