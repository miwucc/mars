/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.sample;

import java.util.Collection;
import java.util.List;

/**
 * 样本实例工厂接口
 * <p>
 * Created on 2017/3/17.
 *
 * @author Alan
 * @since 1.0
 */
public interface SampleFactory<T extends Sample> {
    void addSample(T sample);

    void addSamples(List<T> samples);

    T getSample(int sid);

    T newSample(int sid);

    Collection<T> getAllSamples();

}
