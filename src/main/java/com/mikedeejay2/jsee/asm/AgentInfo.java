package com.mikedeejay2.jsee.asm;

import java.lang.instrument.ClassFileTransformer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgentInfo {
    private final List<ClassFileTransformer> transformers;
    private final List<Class<?>> toRedefineClasses;
    private final List<Class<?>> agentClasses;
    private String JVMPid;

    public AgentInfo() {
        transformers = new ArrayList<>();
        toRedefineClasses = new ArrayList<>();
        agentClasses = new ArrayList<>();
        agentClasses.add(LateBindAttacher.class);
        this.JVMPid = LateBindAttacher.getPidFromRuntimeBean();
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
}
