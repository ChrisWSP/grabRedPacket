package com.wsp.rp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Description:
 *
 * @Author: weishenpeng
 * Date: 2018/6/10
 * Time: 下午 03:26
 */
public class RedPacketUtils {

	/**
	 * 发红包算法，金额参数以分为单位
	 *
	 * @param totalAmount
	 * @param totalPeopleNum
	 * @return
	 */
	public static List<Integer> divideRedPackage(Integer totalAmount, Integer totalPeopleNum) {
		List<Integer> amountList = new ArrayList<Integer>();
		Integer restAmount = totalAmount;
		Integer restPeopleNum = totalPeopleNum;
		Random random = new Random();

		for (int i = 0; i < totalPeopleNum - 1; i++) {

			// 随机范围：[1，剩余人均金额的两倍)，左闭右开
			int amount = random.nextInt(restAmount / restPeopleNum * 2 - 1) + 1;
			restAmount -= amount;
			restPeopleNum--;
			amountList.add(amount);
		}
		amountList.add(restAmount);
		return amountList;
	}

	public static void main(String[] args) {
		List<Integer> amountList = divideRedPackage(500000, 30000);
		for (Integer amount : amountList) {
			System.out.println("抢到金额：" + new BigDecimal(amount).divide(new BigDecimal(100)));
		}
	}
}