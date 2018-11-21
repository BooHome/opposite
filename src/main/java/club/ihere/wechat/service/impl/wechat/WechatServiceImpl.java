package club.ihere.wechat.service.impl.wechat;

import club.ihere.common.api.MessageAPI;
import club.ihere.common.api.UserAPI;
import club.ihere.common.api.response.GetUserInfoResponse;
import club.ihere.common.api.response.GetUsersResponse;
import club.ihere.common.message.BaseMsg;
import club.ihere.common.message.TextMsg;
import club.ihere.common.message.req.BaseEvent;
import club.ihere.common.message.req.TextReqMsg;
import club.ihere.common.util.current.StringUtil;
import club.ihere.wechat.bean.pojo.base.WechatMessage;
import club.ihere.wechat.bean.pojo.base.WechatUsermember;
import club.ihere.wechat.bean.pojo.base.WechatUsermemberExample;
import club.ihere.wechat.common.config.WeChatConfig;
import club.ihere.wechat.common.enums.WechatUserEnums;
import club.ihere.wechat.mapper.base.WechatMessageMapper;
import club.ihere.wechat.mapper.base.WechatUsermemberMapper;
import club.ihere.wechat.service.wechat.WechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: fengshibo
 * @date: 2018/11/20 17:16
 * @description:
 */
@Service
public class WechatServiceImpl implements WechatService {

    private static Logger logger = LoggerFactory.getLogger(WechatServiceImpl.class);

    private static final String USER_WECHAT_SESSION_REDIS_KEY = "userWechatSession:";

    private static final Integer USER_WECHAT_SESSION_REDIS_KEY_DATE = 23;

    private static final String USER_WECHAT_MESSAGE_KEY = "userMessage:";

    private static final String USER_MESSAGE_TYPE = "树洞树洞";

    @Autowired(required = false)
    private WechatUsermemberMapper wechatUsermemberMapper;

    @Autowired(required = false)
    private WechatMessageMapper wechatMessageMapper;

    @Autowired
    @Qualifier("baseRedisTemplate")
    private RedisTemplate redisTemplate;


    @Override
    public BaseMsg saveBySubscribe(BaseEvent event) throws UnsupportedEncodingException {
        String fromUserName = event.getFromUserName();
        WechatUsermember wechatUsermember = this.saveWechatUsermember(fromUserName);
        return new TextMsg("亲爱的" + wechatUsermember.getNickname() + ",感谢您的关注！");
    }

    private WechatUsermember saveWechatUsermember(String fromUserName) throws UnsupportedEncodingException {
        UserAPI userAPI = new UserAPI(WeChatConfig.apiConfig);
        GetUserInfoResponse userInfo = userAPI.getUserInfo(fromUserName);
        WechatUsermemberExample wechatUsermemberExample = new WechatUsermemberExample();
        wechatUsermemberExample.createCriteria().andOpenidEqualTo(fromUserName);
        List<WechatUsermember> wechatUsermembers = wechatUsermemberMapper.selectByExample(wechatUsermemberExample);
        WechatUsermember wechatUsermember = new WechatUsermember();
        wechatUsermember.setCity(userInfo.getCity());
        wechatUsermember.setCountry(userInfo.getCountry());
        wechatUsermember.setHeadimgurl(userInfo.getHeadimgurl());
        wechatUsermember.setLanguage(userInfo.getLanguage());
        wechatUsermember.setNickname(userInfo.getNickname());
        wechatUsermember.setOpenid(userInfo.getOpenid());
        wechatUsermember.setProvince(userInfo.getProvince());
        wechatUsermember.setSex(userInfo.getSex());
        wechatUsermember.setStatus(WechatUserEnums.UserStatusEnum.OPEN_STATUS.getValue());
        wechatUsermember.setSubscribe(userInfo.getSubscribe());
        wechatUsermember.setSubscribeScene(userInfo.getSubscribeScene());
        wechatUsermember.setSubscribeTime(new Date(userInfo.getSubscribeTime()));
        if (wechatUsermembers != null && wechatUsermembers.size() > 0) {
            wechatUsermember.setId(wechatUsermembers.get(0).getId());
            wechatUsermemberMapper.updateByPrimaryKeySelective(wechatUsermember);
        } else {
            wechatUsermemberMapper.insertSelective(wechatUsermember);
        }
        return wechatUsermember;
    }

    @Override
    public List<String> getUserList() {
        UserAPI userAPI = new UserAPI(WeChatConfig.apiConfig);
        GetUsersResponse users = userAPI.getUsers(null);
        GetUsersResponse.Openid data = users.getData();
        String[] openid = data.getOpenid();
        List<String> stringList = Arrays.asList(openid);
        while (!stringList.get(stringList.size() - 1).equals(users.getNextOpenid()) && StringUtil.isNotBlank(users.getNextOpenid())) {
            GetUsersResponse usersWhile = userAPI.getUsers(users.getNextOpenid());
            GetUsersResponse.Openid dataWhile = users.getData();
            String[] openidWhile = data.getOpenid();
            stringList.addAll(Arrays.asList(openidWhile));
        }
        return stringList;
    }

    @Override
    public List<String> getUserListByNextOpenid(String nextOpenid, Integer size) {
        UserAPI userAPI = new UserAPI(WeChatConfig.apiConfig);
        GetUsersResponse users = userAPI.getUsers(StringUtil.isNotBlank(nextOpenid) ? nextOpenid : null);
        GetUsersResponse.Openid data = users.getData();
        String[] openid = data.getOpenid();
        List<String> stringList = Arrays.asList(openid);
        List<String> result = stringList.subList(0, size - 1);
        return result;
    }

    @Override
    public Integer saveUserList() throws UnsupportedEncodingException {
        List<String> userList = this.getUserList();
        for (String openid : userList) {
            WechatUsermember wechatUsermember = this.saveWechatUsermember(openid);
        }
        return userList.size();
    }

    @Override
    public void saveUserAsMessage(String openid) {
        String key = USER_WECHAT_SESSION_REDIS_KEY + openid;
        redisTemplate.opsForValue().set(key, openid, USER_WECHAT_SESSION_REDIS_KEY_DATE, TimeUnit.HOURS);
    }

    @Override
    public List<String> getUserAsMessage() {
        Set<String> keys = redisTemplate.keys(USER_WECHAT_SESSION_REDIS_KEY + "*");
        List<String> list = redisTemplate.opsForValue().multiGet(keys);
        return list;
    }

    @Override
    public Map<String, String> getUserToUser(String openId) {
        Map<String, String> map = new HashMap<>();
        Object o = redisTemplate.opsForValue().get(openId);
        if (o == null || StringUtil.hasBlank(o.toString())) {
            List<String> userAsMessage = getUserAsMessage();
            if (userAsMessage == null || userAsMessage.size() == 0) {
                return null;
            }
            userAsMessage.remove(openId);
            if (userAsMessage == null || userAsMessage.size() == 0) {
                return null;
            }
            Random random = new Random();
            String toUserOpenId = userAsMessage.get(random.nextInt(userAsMessage.size()));
            redisTemplate.delete(USER_WECHAT_SESSION_REDIS_KEY + openId);
            redisTemplate.delete(USER_WECHAT_SESSION_REDIS_KEY + toUserOpenId);
            map.put(openId, toUserOpenId);
            redisTemplate.opsForValue().set(openId, toUserOpenId, USER_WECHAT_SESSION_REDIS_KEY_DATE, TimeUnit.HOURS);
            redisTemplate.opsForValue().set(toUserOpenId, openId, USER_WECHAT_SESSION_REDIS_KEY_DATE, TimeUnit.HOURS);
        } else {
            map.put(openId, o.toString());
        }
        return map;
    }

    @Override
    public BaseMsg receiveTextMsg(TextReqMsg msg) {
        String openID = msg.getFromUserName();
        WechatMessage wechatMessage=new WechatMessage();
        wechatMessage.setOpenid(openID);
        wechatMessage.setMessage(msg.getContent());
        wechatMessageMapper.insertSelective(wechatMessage);
        if ("清除".equals(msg.getContent())) {
            Map<String, String> userToUser = getUserToUser(openID);
            if (userToUser == null) {
                redisTemplate.delete(openID);
                redisTemplate.delete(USER_WECHAT_MESSAGE_KEY + openID);
                redisTemplate.delete(USER_WECHAT_SESSION_REDIS_KEY + openID);
                return new TextMsg("清除历史消息啦!");
            } else {
                redisTemplate.delete(openID);
                redisTemplate.delete(userToUser.get(openID));
                redisTemplate.delete(USER_WECHAT_MESSAGE_KEY + openID);
                redisTemplate.delete(USER_WECHAT_SESSION_REDIS_KEY + openID);
                saveUserAsMessage(userToUser.get(openID));
                MessageAPI messageAPI = new MessageAPI(WeChatConfig.apiConfig);
                messageAPI.sendCustomMessage(userToUser.get(openID), new TextMsg("[" + getNickName(openID) + "]清空消息了哦！"));
                return new TextMsg("清除历史消息啦!");
            }
        } else {
            if (USER_MESSAGE_TYPE.equals(msg.getContent())) {
                redisTemplate.opsForValue().set(USER_WECHAT_MESSAGE_KEY + openID, msg.getContent(), USER_WECHAT_SESSION_REDIS_KEY_DATE, TimeUnit.HOURS);
                //树洞树洞
                Map<String, String> userToUser = getUserToUser(openID);
                if (userToUser == null) {
                    saveUserAsMessage(openID);
                    redisTemplate.delete(openID);
                    return new TextMsg("不好意思，目前树洞对面空空如也哦!");
                } else {
                    MessageAPI messageAPI = new MessageAPI(WeChatConfig.apiConfig);
                    messageAPI.sendCustomMessage(userToUser.get(openID), new TextMsg());
                    getBaseMsg("[" + getNickName(openID) + "]和您匹配到了哦!开始对话吧",openID,userToUser);
                    return new TextMsg("[" + getNickName(userToUser.get(openID)) + "]和您匹配到了哦!开始对话吧");
                }
            }
            Object o = redisTemplate.opsForValue().get(USER_WECHAT_MESSAGE_KEY + openID);
            if (o == null || StringUtil.isBlank(o.toString())) {
                redisTemplate.opsForValue().set(USER_WECHAT_MESSAGE_KEY + openID, msg.getContent(), USER_WECHAT_SESSION_REDIS_KEY_DATE, TimeUnit.HOURS);
                //其他
                return new TextMsg("您的消息树洞已经收到啦!");
            } else if (USER_MESSAGE_TYPE.equals(o.toString())) {
                Map<String, String> userToUser = getUserToUser(openID);
                return getBaseMsg(msg.getContent(), openID, userToUser);
            } else {
                //其他
                return new TextMsg("您的消息树洞已经收到啦!");
            }
        }
    }

    @Override
    public String getNickName(String openId) {
        UserAPI userAPI = new UserAPI(WeChatConfig.apiConfig);
        GetUserInfoResponse userInfo = userAPI.getUserInfo(openId);
        return userInfo.getNickname();
    }

    private BaseMsg getBaseMsg(String content, String openID, Map<String, String> userToUser) {
        if (userToUser == null) {
            return new TextMsg("不好意思，目前树洞对面空空如也哦!");
        } else {
            MessageAPI messageAPI = new MessageAPI(WeChatConfig.apiConfig);
            UserAPI userAPI=new UserAPI(WeChatConfig.apiConfig);
            GetUserInfoResponse userInfo = userAPI.getUserInfo(userToUser.get(openID));
            if(WechatUserEnums.SubscribeEnum.OPEN_STATUS.getValue().equals(userInfo.getSubscribe())){
                messageAPI.sendCustomMessage(userToUser.get(openID), new TextMsg(content));
            }else{
                return new TextMsg("不好意思，"+userInfo.getNickname()+"关闭树洞功能了哦!");
            }
            logger.info("用户发送到服务器的内容:{}", content);
            return null;
            //return new TextMsg("您的消息["+getNickName(userToUser.get(openID))+"]已经收到啦!");
        }
    }
}
