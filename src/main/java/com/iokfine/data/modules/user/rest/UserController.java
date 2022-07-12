package com.iokfine.data.modules.user.rest;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Maps;
import com.iokfine.data.exception.CommonException;
import com.iokfine.data.modules.cache.FastRedisService;
import com.iokfine.data.modules.cache.RedisKeyConstant;
import com.iokfine.data.modules.core.domain.annotation.rest.AnonymousPostMapping;
import com.iokfine.data.modules.core.domain.base.RespMsg;
import com.iokfine.data.modules.notify.service.EmailClient;
import com.iokfine.data.modules.security.domain.AuthUserDto;
import com.iokfine.data.modules.security.domain.JwtUserDto;
import com.iokfine.data.modules.security.domain.UserDto;
import com.iokfine.data.modules.security.domain.properties.SecurityProperties;
import com.iokfine.data.modules.security.utils.RsaUtils;
import com.iokfine.data.modules.security.utils.SecurityUtils;
import com.iokfine.data.modules.security.utils.TokenProvider;
import com.iokfine.data.modules.user.dao.model.User;
import com.iokfine.data.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    private final SecurityProperties properties;
    private final TokenProvider tokenProvider;
    private final EmailClient emailClient;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final FastRedisService fastRedisService;
    private final UserService userService;
    @Value("${rsa.private_key}")
    private String privateKey;

    private Map<String,String> verifyCodeMap = Maps.newHashMap();

    @AnonymousPostMapping(value = "/login")
    public RespMsg login(@Validated @RequestBody AuthUserDto authUser) throws Exception {
        // 密码解密
        String password = RsaUtils.decryptByPrivateKey(privateKey, authUser.getPassword());
        UserDto byName = userService.findByName(authUser.getUsername());
        if(byName==null){
            throw new UsernameNotFoundException("");
        }
        String salt = byName.getSecret();
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authUser.getUsername(), password+salt);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 生成令牌与第三方系统获取令牌方式
        // UserDetails userDetails = userDetailsService.loadUserByUsername(userInfo.getUsername());
        // Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        // SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication);
        final JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();
        // 返回 token 与 用户信息
        Map<String, Object> authInfo = new HashMap<String, Object>(2) {{
            put("token", properties.getTokenStartWith() + token);
            put("user", jwtUserDto);
        }};
//        emailClient.gmailSender(jwtUserDto.getUser().getMail(),jwtUserDto.getUsername()+ "账户登录！","查看是否本人");
        return RespMsg.successResult(authInfo);
    }

    @GetMapping(value = "/info")
    public RespMsg getUserInfo() {
        return RespMsg.successResult(SecurityUtils.getCurrentUser());
    }

//    @GetMapping("/code")
//    public ResponseEntity<Object> getCode() {
//        // 获取运算的结果
//        Captcha captcha = loginProperties.getCaptcha();
//        String uuid = properties.getCodeKey() + IdUtil.simpleUUID();
//        //当验证码类型为 arithmetic时且长度 >= 2 时，captcha.text()的结果有几率为浮点型
//        String captchaValue = captcha.text();
//        if (captcha.getCharType() - 1 == LoginCodeEnum.arithmetic.ordinal() && captchaValue.contains(".")) {
//            captchaValue = captchaValue.split("\\.")[0];
//        }
//        // 保存
//        redisUtils.set(uuid, captchaValue, loginProperties.getLoginCode().getExpiration(), TimeUnit.MINUTES);
//        // 验证码信息
//        Map<String, Object> imgResult = new HashMap<String, Object>(2) {{
//            put("img", captcha.toBase64());
//            put("uuid", uuid);
//        }};
//        return ResponseEntity.ok(imgResult);
//    }

    @AnonymousPostMapping(value = "/register")
    public RespMsg logout(@Validated @RequestBody AuthUserDto authUser) throws Exception {
        String cacheKey = String.format(RedisKeyConstant.REG_VERIFY_CODE, authUser.getUsername());
        String code = (String)fastRedisService.get(cacheKey);
        if(!StringUtils.hasText(code)){
            return RespMsg.builder().setCode("000001").setMsg("验证码失效").build();
        }
        if(!code.equals(authUser.getVerifyCode())){
            return RespMsg.builder().setCode("000001").setMsg("验证码失效").build();
        }
        UserDto byName = userService.findByName(authUser.getUsername());
        if(byName != null){
            return RespMsg.successResult("邮箱被占用");
        }
        // 密码解密
        String password = RsaUtils.decryptByPrivateKey(privateKey, authUser.getPwd());
        authUser.setPassword(password);
        userService.addUser(authUser);
        fastRedisService.remove(cacheKey);
        return RespMsg.successResult("status",true);
    }
}
