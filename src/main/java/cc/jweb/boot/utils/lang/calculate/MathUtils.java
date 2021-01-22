/*
 * Copyright (c) 2020-2021 imlzw@vip.qq.com jweb.cc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.jweb.boot.utils.lang.calculate;

import java.math.BigDecimal;

/**
 * 科学计算工具类
 * @author ag777
 * @version create on 2019年01月07日,last modify at 2019年01月16日
 */
public class MathUtils {

	private MathUtils() {}
	
	/**
	 * 除(保留小数点后两位,尾数四舍五入)
	 * @param a a
	 * @param b b
	 * @return
	 */
	public static BigDecimal divide(double a, double b) {
		return divide(a, b,	 2, BigDecimal.ROUND_CEILING);
	}
	
	/**
	 * 除(尾数四舍五入)
	 * @param a a
	 * @param b b
	 * @param scale 保留小数点后几位
	 * @return
	 */
	public static BigDecimal divide(double a, double b, int scale) {
		return divide(a, b,	 scale, BigDecimal.ROUND_CEILING);
	}
	
	/**
	 * 除
	 * @param a a
	 * @param b b
	 * @param scale 保留小数点后几位
	 * @param roundingMode 如<br>
	 * <pre>{@code
		BigDecimal.ROUND_DOWN 截端操作，类似truncate 该模式永远不会增加被操作的数的值
		BigDecimal.ROUND_UP 在精度最后一位加一个单位  setScale(2,BigDecimal.ROUND_UP) 1.666 ->1.67
		BigDecimal.ROUND_CEILING 朝正无穷方向round 如果为正数，行为和round_up一样，如果为负数，行为和round_down一样
		BigDecimal.ROUND_FLOOR 朝负无穷方向round 如果为正数，行为和round_down一样，如果为负数，行为和round_up一样
		BigDecimal.ROUND_HALF_UP 遇到.5的情况时往上近似,例: 1.5 ->2;
		BigDecimal.ROUND_HALF_DOWN 遇到.5的情况时往下近似,例: 1.5 ->;1 注：1.51->2
		BigDecimal.ROUND_HALF_EVEN 如果舍弃部分左边的数字为奇数，则作   ROUND_HALF_UP   ；如果它为偶数，则作   ROUND_HALF_DOWN
	 	}
     * </pre>
	 * @return
	 */
	public static  BigDecimal divide(double a, double b, int scale, int roundingMode) {
		return new BigDecimal(a).divide(new BigDecimal(b), scale, roundingMode);
	}
	
	
	 /**
	  * 使用欧几里得算法求解数m和数n最大公约数
	  * @param m m
	  * @param n n
	  * @return
	  */
   public static int getGcd(int m,int n){
       while(n > 0){
           int temp = m % n;
           m = n;
           n = temp;
       }
       return m;
   }
   
  /**
   * 求两个数的最小公倍数
   * @param m m
   * @param n n
   * @return
   */
   public static int getLcm(int m,int n){
       int gcd = getGcd(m,n);
       int result = m*n / gcd;
       return result;
   }
   
   /**
    * 求n个数的最小公倍数
    * @param m m
    * @param n n
    * @param others others
    * @return
    */
   public static int getLcm(int m, int n, int... others) {
   	int num = getLcm(m, n);
   	for (int i : others) {
   		num = getLcm(num, i);
		}
   	return num;
   }
}