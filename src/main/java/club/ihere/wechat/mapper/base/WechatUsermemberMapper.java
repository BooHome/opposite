package club.ihere.wechat.mapper.base;

import club.ihere.wechat.bean.pojo.base.WechatUsermember;
import club.ihere.wechat.bean.pojo.base.WechatUsermemberExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface WechatUsermemberMapper {
    long countByExample(WechatUsermemberExample example);

    int deleteByExample(WechatUsermemberExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(WechatUsermember record);

    int insertSelective(WechatUsermember record);

    List<WechatUsermember> selectByExample(WechatUsermemberExample example);

    WechatUsermember selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") WechatUsermember record, @Param("example") WechatUsermemberExample example);

    int updateByExample(@Param("record") WechatUsermember record, @Param("example") WechatUsermemberExample example);

    int updateByPrimaryKeySelective(WechatUsermember record);

    int updateByPrimaryKey(WechatUsermember record);
}