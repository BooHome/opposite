package club.ihere.wechat.mapper.base;

import club.ihere.wechat.bean.pojo.base.WechatMessage;
import club.ihere.wechat.bean.pojo.base.WechatMessageExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface WechatMessageMapper {
    int countByExample(WechatMessageExample example);

    int deleteByExample(WechatMessageExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(WechatMessage record);

    int insertSelective(WechatMessage record);

    List<WechatMessage> selectByExample(WechatMessageExample example);

    WechatMessage selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") WechatMessage record, @Param("example") WechatMessageExample example);

    int updateByExample(@Param("record") WechatMessage record, @Param("example") WechatMessageExample example);

    int updateByPrimaryKeySelective(WechatMessage record);

    int updateByPrimaryKey(WechatMessage record);
}