package com.wsp.rp;

import com.sun.deploy.util.StringUtils;
import redis.clients.jedis.Jedis;

/**
 * Description:
 *
 * @Author: weishenpeng
 * Date: 2018/6/10
 * Time: 下午 04:37
 */
public class GrabRedPacket implements Runnable {
	private String grabSha = null;
	private static final String GRAB_EVAL;
	static {
		GRAB_EVAL = "-- 领取人的openid为xxxxxxxxxxx\n" +
				"local openid = ARGV[1]\n" +
				"local isDraw = redis.call('HEXISTS', 'red::draw', openid)\n" +
				"-- 已经领取\n" +
				"if isDraw ~= 0 then\n" +
				"    return true\n" +
				"end\n" +
				"-- 领取太多次了\n" +
				"local times = redis.call('INCR', 'red::draw_count:u:'..openid)\n" +
				"if times and tonumber(times) > 9 then\n" +
				"    return 0\n" +
				"end\n" +
				"local number = redis.call('RPOP', 'red::list')\n" +
				"-- 没有红包\n" +
				"if not number then\n" +
				"    return {}\n" +
				"end\n" +
				"-- 领取人昵称为Fhb,头像为https://xxxxxxx\n" +
				"local red = {money=number,name=openid,pic='https://xxxxxxx'}\n" +
				"-- 领取记录\n" +
				"redis.call('HSET', 'red::draw', openid, cjson.encode(red))\n" +
				"-- 处理队列\n" +
				"red['openid'] = openid\n" +
				"redis.call('RPUSH', 'red::task', cjson.encode(red))\n" +
				"return true\n";
	}
	/**
	 * 发送人ID
	 */
	private String sendId;
	/**
	 * 抢购人ID
	 */
	private String grabId;

	public GrabRedPacket(String sendId, String grabId) {
		this.sendId = sendId;
		this.grabId = grabId;
	}

	private boolean grapRedPacket(String sendId, String grabId) {
		Jedis jedis = RedisPoolUtils.getConn();
		try {
			if (grabSha == null || !jedis.scriptExists(grabSha)) {
				grabSha = jedis.scriptLoad(GRAB_EVAL);
			}
			System.out.println("grabSha: " + grabSha);
			Object result = jedis.evalsha(grabSha, 0, grabId);
			System.out.println("Grab Red Packet: " + grabId + ", " + result);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			RedisPoolUtils.closeConn();
		}
		return true;
	}


	@Override
	public void run() {
		grapRedPacket(sendId, grabId);
	}
}
