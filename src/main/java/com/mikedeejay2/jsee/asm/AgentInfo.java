package com.mikedeejay2.jsee.asm;

import java.lang.instrument.ClassFileTransformer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgentInfo {
    public static Class<?> defaultAgent = JSEEAgent.class;
    public static String defaultArgs = "";

    private final List<ClassFileTransformer> transformers;
    private final List<Class<?>> toRedefineClasses;
    private final List<Class<?>> agentClasses;
    private String additionalArgs;
    private Class<?> agentMain;
    private String JVMPid;

    public AgentInfo() {
        this.transformers = new ArrayList<>();
        this.toRedefineClasses = new ArrayList<>();
        this.agentClasses = new ArrayList<>();
        this.agentClasses.add(defaultAgent);
        this.JVMPid = LateBindAttacher.getPidFromRuntimeBean();
        this.agentMain = defaultAgent;
        this.additionalArgs = defaultArgs;
    }

    public AgentInfo addTransformers(ClassFileTransformer... transformers) {
        this.transformers.addAll(Arrays.asList(transformers));
        return this;
    }

    public AgentInfo addClassesToRedefine(Class<?>... classes) {
        this.toRedefineClasses.addAll(Arrays.asList(classes));
        return this;
    }

    public AgentInfo addAgentClasses(Class<?>... classes) {
        this.agentClasses.addAll(Arrays.asList(classes));
        return this;
    }

    public List<ClassFileTransformer> getTransformers() {
        return transformers;
    }

    public List<Class<?>> getClassesToRedefine() {
        return toRedefineClasses;
    }

    public List<Class<?>> getAgentClasses() {
        return agentClasses;
    }

    public String getJVMPid() {
        return JVMPid;
    }

    public void setJVMPid(String JVMPid) {
        this.JVMPid = JVMPid;
    }

    public Class<?> getAgentMain() {
        return agentMain;
    }

    public void setAgentMain(Class<?> agentMain) {
        this.agentMain = agentMain;
    }

    public String getAdditionalArgs() {
        return additionalArgs;
    }

    public void setAdditionalArgs(String additionalArgs) {
        this.additionalArgs = additionalArgs;
    }

    public static Class<?> getDefaultAgent() {
        return defaultAgent;
    }

    public static void setDefaultAgent(Class<?> defaultAgent) {
        AgentInfo.defaultAgent = defaultAgent;
    }

    public static String getDefaultArgs() {
        return defaultArgs;
    }

    public static void setDefaultArgs(String defaultArgs) {
        AgentInfo.defaultArgs = defaultArgs;
    }
}
