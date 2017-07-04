/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.maker;

import javassist.CtClass;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created on 2017/4/19.
 *
 * @author Chow
 * @since 1.0
 */
public class ScriptInfo {

    private CtClass scriptClass;

    private String mappingClass;

    private Map<String,Method> scriptMethods;

    private Object bean;

    public ScriptInfo(CtClass scriptClass, String mappingClass, Map<String, Method> scriptMethods, Object bean) {
        this.scriptClass = scriptClass;
        this.mappingClass = mappingClass;
        this.scriptMethods = scriptMethods;
        this.bean = bean;
    }

    public CtClass getScriptClass() {
        return scriptClass;
    }

    public void setScriptClass(CtClass scriptClass) {
        this.scriptClass = scriptClass;
    }

    public String getMappingClass() {
        return mappingClass;
    }

    public void setMappingClass(String mappingClass) {
        this.mappingClass = mappingClass;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Map<String, Method> getScriptMethods() {
        return scriptMethods;
    }

    public void setScriptMethods(Map<String, Method> scriptMethods) {
        this.scriptMethods = scriptMethods;
    }
}
