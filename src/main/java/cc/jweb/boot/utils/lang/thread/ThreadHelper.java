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

package cc.jweb.boot.utils.lang.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Callable;

/**
 * 多线程工具类(只辅助,外部靠内存共享数据实现结果处理)
 *
 * @author ag777
 * Time: created at 2017/3/21. last modify at 2017/4/17.
 */
public class ThreadHelper<T> implements TaskHelperInterf<T> {

    private List<Thread> threadList;
    private List<T> result;

    public ThreadHelper() {
        threadList = new ArrayList<Thread>();
        result = new Vector<T>();
    }

    /*==============添加单个任务==============*/

    /*===静态方法===*/
    public static void startThread(final Callable<Void> t) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    t.call();
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }).start();
    }

    /*==============添加多个任务==============*/

    public static void main(String[] args) {

        List<Callable<Integer>> cs = new ArrayList<Callable<Integer>>();

        for (int i = 0; i < 20; i++) {
            cs.add(new Callable<Integer>() {

                @Override
                public Integer call() throws Exception {
                    int j = new Random().nextBoolean() ? 1 : 5;
                    Thread.sleep(100 * j);
                    System.out.println(j);
                    return j;
                }
            });
        }

        new ThreadHelper<Integer>().addTasks(cs).getResult();
    }

    @Override
    public ThreadHelper<T> addTask(final Callable<T> callable) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    result.add(callable.call());
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        });
        threadList.add(t);
        t.start();
        return this;
    }

    @Override
    public ThreadHelper<T> addTasks(List<Callable<T>> callables) {
        for (Callable<T> callable : callables) {
            addTask(callable);
        }
        return this;
    }

    private ThreadHelper<T> join() {
        for (Thread t : threadList) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public List<T> getResult() {
        join();
        return result;
    }
}
