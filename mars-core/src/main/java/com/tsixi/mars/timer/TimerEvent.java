/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2017/3/15.
 *
 * @author Alan
 * @since 1.0
 */
public class TimerEvent<T> implements Runnable {
    /**
     * 定义无限循环常量
     */
    public static final int INFINITE_CYCLE = 0x7fffffff;

    public static int debugTime = 1000;

    /**
     * 日志记录
     */
    private static final Logger log = LoggerFactory.getLogger(TimerEvent.class);

    /* fields */
    /**
     * 定时事件监听器
     */
    protected TimerListener listener;

    /**
     * 事件动作参数
     */
    protected T parameter;

    /**
     * 间隔时间
     */
    protected int intervalTime;

    /**
     * 间隔时间单位
     */
    protected TimeUnit timeUnit;

    /**
     * 定时次数
     */
    protected int count;

    /**
     * 初始延迟时间
     */
    protected int initTime;

    /**
     * 决对时间定时，线程工作的时间计算在定时时间内
     */
    protected boolean absolute;

    /**
     * 起始时间
     */
    protected long startTime;

    /**
     * 当前运行的时间
     */
    protected long currentTime;

    /**
     * 下一次运行的时间
     */
    protected long nextTime;

    /**
     * 是否在 fire 中
     */
    protected volatile boolean inFire;

    /**
     * 是否启动或者禁用
     */
    protected volatile boolean enabled;

    /* constructors */

    protected TimerEvent(TimerListener listener, T parameter) {
        this.listener = listener;
        this.parameter = parameter;
    }

    /**
     * 构造一个定时事件对象，默认为无初始延迟时间、无限循环
     */
    public TimerEvent(TimerListener listener, T parameter, int intervalTime,
                      boolean absolute) {
        this(listener, parameter, intervalTime, INFINITE_CYCLE, 0, absolute);
    }

    /**
     * 构造一个定时事件对象，默认为无初始延迟时间、无限循环，相对时间定时
     */
    public TimerEvent(TimerListener listener, T parameter, int intervalTime) {
        this(listener, parameter, intervalTime, INFINITE_CYCLE, 0, false);
    }

    /**
     * 构造一个定时事件对象，默认为无初始延迟时间，相对时间定时
     */
    public TimerEvent(TimerListener listener, T parameter, int intervalTime,
                      int count) {
        this(listener, parameter, intervalTime, count, 0, false);
    }

    /**
     * 构造一个定时事件对象，默认为无初始延迟时间
     */
    public TimerEvent(TimerListener listener, T parameter, int intervalTime,
                      int count, boolean absolute) {
        this(listener, parameter, intervalTime, count, 0, absolute);
    }

    /**
     * 构造一个定时事件对象，默认为相对时间定时
     */
    public TimerEvent(TimerListener listener, T parameter, int intervalTime,
                      int count, int initTime) {
        this(listener, parameter, intervalTime, count, initTime, false);
    }

    /**
     * 构造一个定时事件对象，设置为指定时间开始 , 若设置时间小于当前时间则立刻开始
     */
    public TimerEvent(TimerListener listener, long time, T parameter) {
        this.listener = listener;
        this.nextTime = time;
        this.parameter = parameter;
        this.count=1;
    }

    /**
     * 构造一个定时源事件对象
     *
     * @param listener     定时事件监听器
     * @param parameter    事件动作参数
     * @param intervalTime 定时时间
     * @param count        定时次数
     * @param initTime     初始延迟时间
     * @param absolute
     */
    public TimerEvent(TimerListener listener, T parameter, int intervalTime,
                      int count, int initTime, boolean absolute) {
        this.listener = listener;
        this.parameter = parameter;
        this.intervalTime = intervalTime;
        this.count = count;
        this.initTime = initTime;
        this.absolute = absolute;
        this.inFire = false;
        this.enabled = true;
    }

    public TimerEvent withTimeUnit(TimeUnit timeUnit) {
        assert timeUnit != null;
        this.timeUnit = timeUnit;
        this.intervalTime = (int) timeUnit.toMillis(intervalTime);
        this.initTime = (int) timeUnit.toMillis(initTime);
        return this;
    }

    /* properties */

    /**
     * 获得定时事件监听器
     */
    public TimerListener getTimerListener() {
        return listener;
    }

    /**
     * 获得事件动作参数
     */
    public Object getParameter() {
        return parameter;
    }

    /**
     * 设置事件动作参数
     */
    public void setParameter(T parameter) {
        this.parameter = parameter;
    }

    /**
     * 获得定时时间
     */
    public int getIntervalTime() {
        return intervalTime;
    }

    /**
     * 设置定时时间
     */
    public void setIntervalTime(int time) {
        intervalTime = time;
    }

    /**
     * 获得定时次数
     */
    public int getCount() {
        return count;
    }

    /**
     * 设置定时次数
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * 设置是否启动或禁用
     */
    public void setEnabled(boolean b) {
        enabled = b;
    }

    /**
     * 获取是否启用或禁用
     */
    public boolean getEnabled() {
        return enabled;
    }

    /**
     * 获得定时的起始延时时间
     */
    public int getInitTime() {
        return initTime;
    }

    /**
     * 设置定时的起始延时时间
     */
    public void setInitTime(int initTime) {
        this.initTime = initTime;
    }

    /**
     * 判断是否为决对时间定时
     */
    public boolean isAbsolute() {
        return absolute;
    }

    /**
     * 设置决对或相对时间定时
     */
    public void setAbsolute(boolean b) {
        absolute = b;
    }

    /**
     * 获得起始时间
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * 获得当前运行的时间
     */
    public long getCurrentTime() {
        return currentTime;
    }

    /**
     * 获得下一次运行的时间
     */
    public long getNextTime() {
        return nextTime;
    }

    /**
     * 设置下一次运行的时间
     */
    public void setNextTime(long time) {
        nextTime = time;
    }

    /* methods */

    /**
     * 初始化方法
     */
    public void init() {
        if (nextTime == 0){
            nextTime = startTime + initTime;
        }
        startTime = System.currentTimeMillis();
        enabled = true;
    }

    /**
     * 引发定时事件，通知定时事件监听器，设置定时次数和下一次的运行时间
     */
    void fire(long currentTime) {
        if (count != INFINITE_CYCLE)
            count--;
        this.currentTime = currentTime;
        nextTime = absolute ? (nextTime + intervalTime)
                : (currentTime + intervalTime);
        try {
            listener.onTimer(this);
        } catch (Throwable e) {
            if (log.isWarnEnabled())
                log.warn("fire error, " + toString(), e);
        }
        long endTime = System.currentTimeMillis();

        // 添加对执行超过1秒的事件进行打印
        if (endTime - currentTime > debugTime) {
            log.warn("event fire long time,event=" + toString());
        }
        inFire = false;
    }

    /* common methods */
    public String toString() {
        return super.toString() + "[listener=" + listener + ", parameter="
                + parameter + ", intervalTime=" + intervalTime + ", count="
                + count + ", initTime=" + initTime + ", absolute=" + absolute
                + "]";
    }

    @Override
    public void run() {
        fire(System.currentTimeMillis());
    }
}
