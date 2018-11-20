package club.ihere.wechat.service.impl.wechat;

import club.ihere.common.api.UserAPI;
import club.ihere.common.api.response.GetUserInfoResponse;
import club.ihere.common.api.response.GetUsersResponse;
import club.ihere.common.message.BaseMsg;
import club.ihere.common.message.TextMsg;
import club.ihere.common.message.req.BaseEvent;
import club.ihere.common.message.req.TextReqMsg;
import club.ihere.common.util.current.StringUtil;
import club.ihere.wechat.bean.pojo.base.WechatUsermember;
import club.ihere.wechat.bean.pojo.base.WechatUsermemberExample;
import club.ihere.wechat.common.config.WeChatConfig;
import club.ihere.wechat.common.enums.WechatUserEnums;
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
import java.util.List;
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

    @Autowired(required = false)
    private WechatUsermemberMapper wechatUsermemberMapper;

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
    public List getUserAsMessage() {
        Set<String> keys = redisTemplate.keys(USER_WECHAT_SESSION_REDIS_KEY + "*");
        List list = redisTemplate.opsForValue().multiGet(keys);
        return null;
    }

    @Override
    public BaseMsg receiveTextMsg(TextReqMsg msg) {
        String openID=msg.getFromUserName();
        this.saveUserAsMessage(openID);
        String content = msg.getContent();
        logger.info("用户发送到服务器的内容:{}", content);
        return new TextMsg("服务器回复用户消息!");
    }
}
