package com.wsp.rp;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Properties;

/**
 * Description:
 *
 * @Author: weishenpeng
 * Date: 2018/6/10
 * Time: 下午 02:38
 */
public class RedisPoolUtils {
	private static JedisPool jedisPool = null;
	private static String redisConfigFile = "redis.properties";

	private static ThreadLocal<Jedis> local = new ThreadLocal<Jedis>();


	private RedisPoolUtils() {

	}

	/**
	 * 初始化Jedis连接池
	 */
	public static void initialPool() {
		try {
			Properties props = new Properties();
			props.load(RedisPoolUtils.class.getResourceAsStream(redisConfigFile));

			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(Integer.valueOf(props.getProperty("jedis.pool.maxActive")));
			config.setMaxIdle(Integer.valueOf(props.getProperty("jedis.pool.maxIdle")));
			config.setMaxWaitMillis(Long.valueOf(props.getProperty("jedis.pool.maxWait")));
			config.setTestOnBorrow(Boolean.valueOf(props.getProperty("jedis.pool.testOnBorrow")));
			config.setTestOnReturn(Boolean.valueOf(props.getProperty("jedis.pool.testOnReturn")));
			// 根据配置实例化jedis池
			jedisPool = new JedisPool(config, props.getProperty("redis.ip"),
					Integer.valueOf(props.getProperty("redis.port")),
					Integer.valueOf(props.getProperty("redis.timeout")));
			System.out.println("线程池被成功初始化");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得连接
	 *
	 * @return Jedis
	 */
	public static Jedis getConn() {
		//Redis对象
		Jedis jedis = local.get();
		if (jedis == null) {
			if (jedisPool == null) {
				initialPool();
			}
			jedis = jedisPool.getResource();
			local.set(jedis);
		}
		return jedis;
	}

	/**
	 * 新版本用close归还连接
	 */
	public static void closeConn() {
		//从本地线程中获取
		Jedis jedis = local.get();
		if (jedis != null) {
			jedis.close();
		}
		local.set(null);
	}

	/**
	 * 关闭池
	 */
	public static void closePool() {
		if (jedisPool != null) {
			jedisPool.close();
		}
	}
}
