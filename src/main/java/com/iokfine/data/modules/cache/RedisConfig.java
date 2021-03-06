package com.iokfine.data.modules.cache;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.port}")
	private Integer port;

	@Value("${spring.redis.password}")
	private String password;

	@Value("${spring.redis.timeout}")
	private Integer timeout;

	@Value("${spring.redis.database}")
	private Integer database;

//	@Bean
//	public KeyGenerator wiselyKeyGenerator() {
//
//		return (target, method, params) -> {
//
//			StringBuilder sb = new StringBuilder();
//			sb.append(target.getClass().getName());
//			sb.append(method.getName());
//
//			for (Object object : params) {
//				sb.append(object.toString());
//			}
//			return sb.toString();
//		};
//	}

	@Primary
	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {
//		JedisConnectionFactory factory = new JedisConnectionFactory();
//		factory.setHostName(host);
//		factory.setPort(port);
//		factory.setPassword(password);
//		factory.setTimeout(timeout); // ????????????????????????
//		factory.setDatabase(database);
//		return factory;

		RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
		redisConfig.setHostName(host);
		redisConfig.setPort(port);
		redisConfig.setPassword(password);
		redisConfig.setDatabase(database);
		LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig);
		return factory;
	}

//	@Bean
//	public CacheManager cacheManager(RedisTemplate<String, Object> redisTemplate) {
//		RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
//		// Number of seconds before expiration. Defaults to unlimited (0)
//		cacheManager.setDefaultExpiration(10); // ??????key-value????????????
//		return cacheManager;
//	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
				Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(jackson2JsonRedisSerializer);
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.setHashKeySerializer(jackson2JsonRedisSerializer);
		template.setHashValueSerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();

		return template;
	}

	@Bean(name = "normalJedisFactory")
	public LettuceConnectionFactory normalJedisConnFactory() {
		RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
		redisConfig.setHostName(host);
		redisConfig.setPort(port);
		redisConfig.setPassword(password);
		redisConfig.setDatabase(2);
		LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig);
		return factory;

//		JedisConnectionFactory factory = new JedisConnectionFactory();
//		factory.setHostName(host);
//		factory.setPort(port);
//		factory.setPassword(password);
//		factory.setTimeout(timeout); // ????????????????????????
//		factory.setDatabase(2);
//		return factory;
	}

	@Bean(name = "normalRedisTemplate")
	public RedisTemplate<String, Object> normalRedisTemplate(RedisConnectionFactory normalJedisFactory) {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
				Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(normalJedisFactory);
		template.setKeySerializer(jackson2JsonRedisSerializer);
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.setHashKeySerializer(jackson2JsonRedisSerializer);
		template.setHashValueSerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();

		return template;
	}

	/**
	 * RedisTemplate?????????JDK??????????????????,????????????StringRedisTemplate????????????????????????
	 * ?????????????????????json???????????????redis,?????????????????????json??????????????????????????????????????????
	 * ????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean(name = "fastRedisTemplate")
	public RedisTemplate<String, Object> fastRestTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(redisConnectionFactory);

		FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
		// ????????????????????????????????????????????????????????????fastjson?????????bug?????????????????????
		ParserConfig.getGlobalInstance().addAccept("com.youxiuqingnian.");

		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(fastJsonRedisSerializer);
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(fastJsonRedisSerializer);
		return  template;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getPassword() {
		return password;
	}

	public Integer getTimeout() {
		return timeout;
	}


}
