package com.im.user.mapper;

import com.im.user.entity.po.GroupPo;

public interface GroupPoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GroupPo record);

    int insertSelective(GroupPo record);

    GroupPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GroupPo record);

    int updateByPrimaryKey(GroupPo record);
}