package com.wsp.rp;

import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.List;

/**
 * Description: 发红包
 *
 * @Author: weishenpeng
 * Date: 2018/6/10
 * Time: 下午 04:25
 */
public class SendRedPacket implements Runnable {
	/**
	 * 金额
	 */
	private double money;
	/**
	 * 数量
	 */
	private int num;
	/**
	 * 发送人ID
	 */
	private String sendId;

	public SendRedPacket(double money, int num, String sendId) {
		this.money = money;
		this.num = num;
		this.sendId = sendId;
	}

	/**
	 * 发红包
	 *
	 * @param money
	 * @param num
	 * @return
	 */
	private boolean sendRedPacket(double money, int num, String senderId) {
		if (money <= 0 || num <= 0) {
			return false;
		}
		if (money * 100 / num < 1) {
			return false;
		}
		List<Integer> rpList = RedPacketUtils.divideRedPackage((int) (money * 100), num);
		Jedis jedis = RedisPoolUtils.getConn();
		try {
			if(jedis.exists("red::list")) {
				jedis.del("red::list");
			}
			for (Integer mon : rpList) {
				jedis.lpush("red::list", new BigDecimal(mon).divide(new BigDecimal(100)) + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			RedisPoolUtils.closeConn();
		}
		System.out.println("send 成功！！");
		return true;
	}

	@Override
	public void run() {
		sendRedPacket(money, num, sendId);
	}
}
