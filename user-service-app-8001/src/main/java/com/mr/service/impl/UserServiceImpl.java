package com.mr.service.impl;

import com.mr.common.Const;
import com.mr.common.JwtToken;
import com.mr.common.RedisPrefixConst;
import com.mr.constant.TokenHashConst;
import com.mr.entity.vo.UserVo;
import com.mr.exception.BusinessErrorEnum;
import com.mr.mapper.UserMapper;
import com.mr.response.error.BusinessException;
import com.mr.response.error.CommonErrorAdapter;
import com.mr.entity.po.User;
import com.mr.service.IUserService;
import com.mr.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;




@Component
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    @Value("${token.expiration}")
    private int expiration;

    @Resource
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 用户登陆
     */
    @Override
    public String login(String username, String password) throws BusinessException {

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
        {
            return null;
        }
        String passwordInDb = userMapper.selectPasswordByUsername(username);
        if(passwordInDb == null)
        {
            log.warn("A user {} which is not exist tried to login", username);
            return null;
        }


        String md5Password = MD5Util.MD5EncodeUtf8(password);

        if(!md5Password.equals(passwordInDb))
        {
            log.warn("A user {} with invalid password {} tried to login", username, password);
            return null;
        }

        User user = userMapper.selectUserByUsername(username);
        String token = null;
        try {
            token = JwtToken.createToken();
        } catch (Exception e) {
            e.printStackTrace();
        }

        redisTemplate.opsForHash().put(RedisPrefixConst.TOKEN_PREFIX+token, TokenHashConst.USER, user);
        redisTemplate.expire(RedisPrefixConst.TOKEN_PREFIX+token, expiration, TimeUnit.MINUTES);

        return token;
    }

    /**
     * 注册用户
     * @param user
     * @return
     * @throws BusinessException
     */

    @Override
    public boolean register(User user) throws BusinessException {

        //1. 判断参数
        if(StringUtils.isEmpty(user.getUsername()))
        {
            throw new BusinessException(BusinessErrorEnum.USERNAME_EMPTY_ERROR);
        }
        if(StringUtils.isEmpty(user.getEmail()))
        {
            throw new BusinessException(BusinessErrorEnum.EMAIL_EMPTY_ERROR);
        }
        //2. 处理参数

        //2. 添加
        //3. 判断是否插入成功
        //4. 返回


        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            throw new BusinessException(BusinessErrorEnum.REGISTER_FAILED);
        }
        return true;
    }
    private void checkUserParam(User user){
        user.setId(null);
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setDeleted();
        user.se
    }



    /**
     * 用户注册参数校验
     */
    private boolean isValidRegisterUser(User user) throws BusinessException {
        return true;

    }


    @Override
    public UserVo getUserByToken(String token)
    {
        return null;
    }

    @Override
    public UserVo getUserById(Integer userId) throws BusinessException
    {
        return null;
    }

    @Override
    public boolean updateUserInfo(UserVo user) throws BusinessException
    {
        return false;
    }

    @Override
    public boolean resetPassword(String passwordOld, String passwordNew, User user) throws BusinessException
    {
        return false;
    }
}