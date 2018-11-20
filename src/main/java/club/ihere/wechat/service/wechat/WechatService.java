package club.ihere.wechat.service.wechat;

import club.ihere.common.message.BaseMsg;
import club.ihere.common.message.req.BaseEvent;
import club.ihere.common.message.req.TextReqMsg;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author: fengshibo
 * @date: 2018/11/20 17:16
 * @description:
 */
public interface WechatService {
    /**
     * 在用户关注时，进行用户保存
     *
     * @param event
     * @return
     * @throws UnsupportedEncodingException
     */
    BaseMsg saveBySubscribe(BaseEvent event) throws UnsupportedEncodingException;

    /**
     * 获取全部关注用户列表,用户量少时候使用
     *
     * @return
     */
    List<String> getUserList();


    /**
     * 获取关注用户列表,有分页
     *
     * @param nextOpenid
     * @param size
     * @return
     */
    List<String> getUserListByNextOpenid(String nextOpenid, Integer size);


    /**
     * 初始化少量用户信息时使用
     *
     * @return
     */
    Integer saveUserList() throws UnsupportedEncodingException;

    /**
     * 用户与微信公众号发生交互，记录一次openid，时效48小时
     *
     * @param openid
     */
    void saveUserAsMessage(String openid);

    /**
     * 获取有过交互的openid列表
     *
     * @return
     */
    List getUserAsMessage();

    /**
     * 接收文本消息
     * @param msg
     * @return
     */
    BaseMsg receiveTextMsg(TextReqMsg msg);
}
