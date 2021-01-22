/*
 * Copyright  (c) 2020-2021 imlzw@vip.qq.com jweb.cc.
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

package cc.jweb.boot.utils.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * id生成器
 */
public class IDGenerator {
    public static final String TYPE_UUID = "uuid"; // UUID 全局唯一，无序，缺点：索引慢
    public static final String TYPE_SNOWFLAKE = "SnowFlake"; // 雪花算法，基于时间自增，索引快，缺点：与系统时间相关，不可回调系统时间

    public static Map<String, IdGenerator> generateorMap = new HashMap<String, IdGenerator>();


    /**
     * 下一个id
     *
     * @param tableKey
     * @param type
     * @return
     */
    public static String nextId(String tableKey, String type) {
        String key = tableKey + "_" + type;
        IdGenerator idGenerator = generateorMap.get(key);
        if (idGenerator == null) {
            switch (type) {
                case TYPE_SNOWFLAKE:
                    idGenerator = new SnowFlakeIdGenerator(1, 1, 1);
                    break;
                default:
                    idGenerator = new UUIDGenerator();
            }
            generateorMap.put(key, idGenerator);
        }
        return idGenerator.generatorId();
    }

    public static void main(String[] args) {
        System.out.println(IDGenerator.nextId("1", IDGenerator.TYPE_SNOWFLAKE));
        System.out.println(IDGenerator.nextId("1", IDGenerator.TYPE_UUID));
    }


    public static interface IdGenerator {
        String generatorId();
    }

    /**
     * UUID生成器
     */
    public static class UUIDGenerator implements IdGenerator {

        @Override
        public String generatorId() {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    /**
     * 雪花算法id生成器
     */
    public static class SnowFlakeIdGenerator implements IdGenerator {

        //因为二进制里第一个 bit 为如果是 1，那么都是负数，但是我们生成的 id 都是正数，所以第一个 bit 统一都是 0。

        //机器ID  2进制5位  32位减掉1位 31个
        private long workerId;
        //机房ID 2进制5位  32位减掉1位 31个
        private long datacenterId;
        //代表一毫秒内生成的多个id的最新序号  12位 4096 -1 = 4095 个
        private long sequence;
        //设置一个时间初始值    2^41 - 1   差不多可以用69年
        private long twepoch = 1585644268888L;
        //5位的机器id
        private long workerIdBits = 5L;
        //5位的机房id
        private long datacenterIdBits = 5L;
        //每毫秒内产生的id数 2 的 12次方
        private long sequenceBits = 12L;
        // 这个是二进制运算，就是5 bit最多只能有31个数字，也就是说机器id最多只能是32以内
        private long maxWorkerId = -1L ^ (-1L << workerIdBits);
        // 这个是一个意思，就是5 bit最多只能有31个数字，机房id最多只能是32以内
        private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

        private long workerIdShift = sequenceBits;
        private long datacenterIdShift = sequenceBits + workerIdBits;
        private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
        private long sequenceMask = -1L ^ (-1L << sequenceBits);
        //记录产生时间毫秒数，判断是否是同1毫秒
        private long lastTimestamp = -1L;

        /**
         * @param workerId     机器编号
         * @param dataCenterId 机房编号
         * @param sequence
         */
        public SnowFlakeIdGenerator(long workerId, long dataCenterId, long sequence) {

            // 检查机房id和机器id是否超过31 不能小于0
            if (workerId > maxWorkerId || workerId < 0) {
                throw new IllegalArgumentException(
                        String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
            }

            if (dataCenterId > maxDatacenterId || dataCenterId < 0) {

                throw new IllegalArgumentException(
                        String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
            }
            this.workerId = workerId;
            this.datacenterId = dataCenterId;
            this.sequence = sequence;
        }

        /**
         * main 测试类
         *
         * @param args
         */
        public static void main(String[] args) {
            System.out.println(1 & 4596);
            System.out.println(2 & 4596);
            System.out.println(6 & 4596);
            System.out.println(6 & 4596);
            System.out.println(6 & 4596);
            System.out.println(6 & 4596);
            SnowFlakeIdGenerator worker = new SnowFlakeIdGenerator(1, 1, 1);
            for (int i = 0; i < 22; i++) {
                System.out.println(worker.nextId());
            }
        }

        public String generatorId() {
            long l = this.nextId();
            return Long.toString(l);
        }


        public long getWorkerId() {
            return workerId;
        }

        public long getDatacenterId() {
            return datacenterId;
        }

        public long getTimestamp() {
            return System.currentTimeMillis();
        }

        // 这个是核心方法，通过调用nextId()方法，让当前这台机器上的snowflake算法程序生成一个全局唯一的id
        public synchronized long nextId() {
            // 这儿就是获取当前时间戳，单位是毫秒
            long timestamp = timeGen();
            if (timestamp < lastTimestamp) {

                System.err.printf(
                        "clock is moving backwards. Rejecting requests until %d.", lastTimestamp);
                throw new RuntimeException(
                        String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                                lastTimestamp - timestamp));
            }

            // 下面是说假设在同一个毫秒内，又发送了一个请求生成一个id
            // 这个时候就得把seqence序号给递增1，最多就是4096
            if (lastTimestamp == timestamp) {

                // 这个意思是说一个毫秒内最多只能有4096个数字，无论你传递多少进来，
                //这个位运算保证始终就是在4096这个范围内，避免你自己传递个sequence超过了4096这个范围
                sequence = (sequence + 1) & sequenceMask;
                //当某一毫秒的时间，产生的id数 超过4095，系统会进入等待，直到下一毫秒，系统继续产生ID
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }

            } else {
                sequence = 0;
            }
            // 这儿记录一下最近一次生成id的时间戳，单位是毫秒
            lastTimestamp = timestamp;
            // 这儿就是最核心的二进制位运算操作，生成一个64bit的id
            // 先将当前时间戳左移，放到41 bit那儿；将机房id左移放到5 bit那儿；将机器id左移放到5 bit那儿；将序号放最后12 bit
            // 最后拼接起来成一个64 bit的二进制数字，转换成10进制就是个long型
            return ((timestamp - twepoch) << timestampLeftShift) |
                    (datacenterId << datacenterIdShift) |
                    (workerId << workerIdShift) | sequence;
        }

        /**
         * 当某一毫秒的时间，产生的id数 超过4095，系统会进入等待，直到下一毫秒，系统继续产生ID
         *
         * @param lastTimestamp
         * @return
         */
        private long tilNextMillis(long lastTimestamp) {

            long timestamp = timeGen();

            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }

        //获取当前时间戳
        private long timeGen() {
            return System.currentTimeMillis();
        }
    }
}
