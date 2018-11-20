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
import club.ihere.wechat.bean.pojo.base.WechatUsermember;
import club.ihere.wechat.bean.pojo.base.WechatUsermemberExample;
import club.ihere.wechat.common.config.WeChatConfig;
import club.ihere.wechat.common.exception.WechatException;
import club.ihere.wechat.service.wechat.WechatService;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

/**
 * @author: fengshibo
 * @date: 2018/11/19 17:32
 * @description:
 */
@RestController
@RequestMapping("wechat")
public class WechatController extends BaseWechatController {

    private static Logger logger = LoggerFactory.getLogger(WechatController.class);

    @Autowired
    private WechatService wechatService;

    @Override
    protected String getToken() {
        return WeChatConfig.getToken();
    }


    @Override
    protected BaseMsg handleSubscribe(BaseEvent event) {
        try {
            return wechatService.saveBySubscribe(event);
        } catch (UnsupportedEncodingException e) {
            throw new WechatException("字段编码失败");
        }
    }

    @Override
    protected BaseMsg handleTextMsg(TextReqMsg msg) {
        return wechatService.receiveTextMsg(msg);
    }
}
