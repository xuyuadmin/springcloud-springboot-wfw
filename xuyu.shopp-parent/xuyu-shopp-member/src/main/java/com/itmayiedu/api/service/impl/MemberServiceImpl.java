package com.itmayiedu.api.service.impl;

import java.util.Date;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.itmayiedu.api.service.MemberService;
import com.itmayiedu.base.BaseApiService;
import com.itmayiedu.base.ResponseBase;
import com.itmayiedu.constants.Constants;
import com.itmayiedu.dao.MemberDao;
import com.itmayiedu.entity.UserEntity;
import com.itmayiedu.mq.RegisterMailboxProducer;
import com.itmayiedu.utils.MD5Util;
import com.itmayiedu.utils.TokenUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MemberServiceImpl extends BaseApiService implements MemberService {

	@Autowired
	private MemberDao memberDao;
	@Autowired
	private RegisterMailboxProducer registerMailboxProducer;
	@Value("${messages.queue}")
	private String MESSAGESQUEUE;

	@Override
	public ResponseBase findUserById(Long userId) {

		UserEntity user = memberDao.findById(userId);
		if (user == null) {
			return setResultError("未查找到该用户信息");
		}
		return setResultSuccess(user);
	}

	@Override
	public ResponseBase regUser(@RequestBody UserEntity user) {
		// 参数验证
		String password = user.getPassword();
		if (StringUtils.isEmpty(password)) {
			return setResultError("密码不能为空");
		}
		String newPassword = MD5Util.MD5(password);
		user.setPassword(newPassword);
		user.setCreated(new Date());
		user.setUpdated(new Date());
		Integer result = memberDao.insertUser(user);
		if (result <= 0) {
			return setResultError("注册用户信息失败");
		}
		// 采用异步方式发送消息
		String email = user.getEmail();
		String json = emailJson(email);
		log.info("###会员服务推送消息到消息服务平台###json{}", json);
		sendMsg(json);
		return setResultSuccess("用户注册成功");
	}

	private String emailJson(String email) {
		JSONObject rootJson = new JSONObject();
		JSONObject header = new JSONObject();
		header.put("interfaceType", Constants.MSG_EMAIL);
		JSONObject content = new JSONObject();
		content.put("email", email);
		rootJson.put("header", header);
		rootJson.put("content", content);
		return rootJson.toJSONString();

	}

	private void sendMsg(String json) {
		// 创建队列
		ActiveMQQueue activeMQQueue = new ActiveMQQueue(MESSAGESQUEUE);
		registerMailboxProducer.sendMsg(activeMQQueue, json);
	}

	@Override
	public ResponseBase login(@RequestBody UserEntity user) {
		// 1.验证参数
		String username = user.getUsername();
		if (StringUtils.isEmpty(username)) {
			return setResultError("用户名称不能为空！");
		}
		String password = user.getPassword();
		if (StringUtils.isEmpty(password)) {
			return setResultError("密码不能为空！");
		}
		// 2.数据库查找账号密码是否正确
		String newPassword = MD5Util.MD5(password);
		UserEntity userEntity = memberDao.login(username, newPassword);
		return setLogin(userEntity);
	}

	private ResponseBase setLogin(UserEntity userEntity) {
		if (userEntity == null) {
			return setResultError("账号或密码不正确！");
		}
		// 3.如果账号密码正确，对应生成token
		String memberToken = TokenUtils.getMemberToken();
		// 4.存在redis中，key为token value为userid
		Integer userId = userEntity.getId();
		log.info("##用户信息token存放在redis中。。key为：{},value", memberToken, userId);
		baseRedisService.setString(memberToken, userId + "", Constants.TOKEN_MEMBER_TIME);
		// 5.直接返回memberToken
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("memberToken", memberToken);
		return setResultSuccess(jsonObject);
	}

	@Override
	public ResponseBase findByToken(@RequestParam("token") String token) {
		// 1.验证参数
		if (StringUtils.isEmpty(token)) {
			return setResultError("token不能为空！");
		}
		// 2.从redis中使用token查找对应userid
		String strUserId = baseRedisService.getString(token);
		if (StringUtils.isEmpty(strUserId)) {
			return setResultError("token无效或者已经过期！");
		}
		// 3.使用userid去数据库查找用户信息返回给客户端
		Long userId = Long.parseLong(strUserId);
		UserEntity userEntity = memberDao.findById(userId);
		if (userEntity == null) {
			return setResultError("未查找到该用户信息！");
		}
		// 密码置空
		userEntity.setPassword(null);
		return setResultSuccess(userEntity);
	}

	@Override
	public ResponseBase findByOpenIdUser(@RequestParam("openid") String openid) {
		// 1.验证参数
		if (StringUtils.isEmpty(openid)) {
			return setResultError("openid不能为空！");
		}
		// 2.使用openid查询数据库user表对应数据信息
		UserEntity userEntity = memberDao.findByOpenIdUser(openid);
		if (userEntity == null) {
			return setResultError(Constants.HTTP_RES_CODE_201, "该openid没有关联！");
		}
		// 3.自动登录
		ResponseBase setLogin = setLogin(userEntity);
		return setLogin;
	}

	@Override
	public ResponseBase qqLogin(@RequestBody UserEntity user) {
		// 1.验证参数
		String openid = user.getOpenid();
		if (StringUtils.isEmpty(openid)) {
			return setResultError("openid不能为空！");
		}
		// 2.先进行账号登录
		ResponseBase setLogin = login(user);
		if (!setLogin.getRtnCode().equals(Constants.HTTP_RES_CODE_200)) {
			// 把错误信息返回给客户端
			return setLogin;
		}
		//3.内部没有通讯还是本身
		JSONObject jsonObjcet = (JSONObject) setLogin.getData();
		//4.获取token信息
		String memberToken = jsonObjcet.getString("memberToken");
		ResponseBase userToken = findByToken(memberToken);
		if(!userToken.getRtnCode().equals(Constants.HTTP_RES_CODE_200)) {
			return userToken;
		}
		UserEntity userEntity = (UserEntity) userToken.getData();
		// 3.如果登录成功，数据库修改对应的openid
		Integer userId = userEntity.getId();
		Integer updateByOpenIdUser = memberDao.updateByOpenIdUser(openid, userId);
		if (updateByOpenIdUser <= 0) {
			return setResultError("qq账号关联失败！");
		}
		return setLogin;
	}
}
