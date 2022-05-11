package com.mikedeejay2.jsee.asm;

import com.mikedeejay2.jsee.JSEE;

import java.lang.instrument.ClassFileTransformer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A data class to hold all information about an agent. Used during runtime to retrieve information between
 * {@link LateBindAttacher} and {@link JSEEAgent} (or similar) classes.
 *
 * @author Mikedeejay2
 * @since 1.0.0
 */
public class AgentInfo implements Cloneable {
    /**
     * The default <code>AgentInfo</code>. All information from this agent is copied into all new agents. This is useful
     * for globally changing the information of new agents.
     */
    public static final AgentInfo DEFAULT = new AgentInfo(JSEEAgent.class);

    /**
     * The list of {@link ClassFileTransformer}s of the agent
     * @since 1.0.0
     */
    private List<ClassFileTransformer> transformers;

    /**
     * The list of classes to be redefined by this agent
     * @since 1.0.0
     */
    private List<Class<?>> toRedefineClasses;

    /**
     * The list of agent's classes to be included in the agent jar
     * @since 1.0.0
     */
    private List<Class<?>> agentClasses;

    /**
     * The agent's arguments to be passed to the <code>agentmain()</code> method
     * @since 1.0.0
     */
    private String additionalArgs;

    /**
     * The <code>Class</code> that contains the <code>agentmain()</code> method
     * @since 1.0.0
     */
    private Class<?> agentMain;

    /**
     * The Java Virtual Machine process ID to attach the agent to. In 99.9% of cases this is the PID of the current JVM.
     * @since 1.0.0
     */
    private String JVMPid;

    /**
     * {@link AgentInfo} constructor, see the following methods to set other required info:
     * <ul>
     *     <li>{@link AgentInfo#addClassesToRedefine(Class[])} or {@link AgentInfo#addClassesToRedefine(String...)}</li>
     *     <li>{@link AgentInfo#addAgentClasses(Class[])} or {@link AgentInfo#addAgentClasses(String...)}</li>
     * </ul>
     *
     * @param transformer The agent's {@link ClassFileTransformer}
     * @since 1.0.0
     */
    public AgentInfo(ClassFileTransformer transformer) {
        this.transformers = new ArrayList<>(DEFAULT.transformers);
        this.toRedefineClasses = new ArrayList<>(DEFAULT.toRedefineClasses);
        this.agentClasses = new ArrayList<>(DEFAULT.agentClasses);
        this.JVMPid = LateBindAttacher.getPidFromRuntimeBean();
        this.agentMain = DEFAULT.agentMain;
        this.additionalArgs = DEFAULT.additionalArgs;
        if(transformer != null) {
            addTransformers(transformer);
            addAgentClasses(transformer.getClass());
        }
    }

    /**
     * {@link AgentInfo} constructor, see the following methods to set other required info:
     * <ul>
     *     <li>{@link AgentInfo#addTransformers(ClassFileTransformer...)}</li>
     *     <li>{@link AgentInfo#addClassesToRedefine(Class[])} or {@link AgentInfo#addClassesToRedefine(String...)}</li>
     *     <li>{@link AgentInfo#addAgentClasses(Class[])} or {@link AgentInfo#addAgentClasses(String...)}</li>
     * </ul>
     * @since 1.0.0
     */
    public AgentInfo() {
        this((ClassFileTransformer) null);
    }

    private AgentInfo(Class<?> agentMain) {
        this.transformers = new ArrayList<>();
        this.toRedefineClasses = new ArrayList<>();
        this.agentClasses = new ArrayList<>();
        this.agentClasses.add(agentMain);
        this.JVMPid = LateBindAttacher.getPidFromRuntimeBean();
        this.agentMain = agentMain;
        this.additionalArgs = "";
    }

    /**
     * Add {@link ClassFileTransformer}s of the agent
     *
     * @param transformers The transformers to add
     * @return This <code>AgentInfo</code>
     * @since 1.0.0
     */
    public AgentInfo addTransformers(ClassFileTransformer... transformers) {
        this.transformers.addAll(Arrays.asList(transformers));
        return this;
    }

    /**
     * Add classes to be redefined by the agent
     *
     * @param classes The classes to add
     * @return This <code>AgentInfo</code>
     * @since 1.0.0
     */
    public AgentInfo addClassesToRedefine(Class<?>... classes) {
        this.toRedefineClasses.addAll(Arrays.asList(classes));
        return this;
    }

    /**
     * Add class names to be redefined by the agent. These classes will be found through {@link Class#forName(String)}
     *
     * @param classNames The class names to add
     * @return This <code>AgentInfo</code>
     * @since 1.0.0
     */
    public AgentInfo addClassesToRedefine(String... classNames) {
        List<Class<?>> classes = new ArrayList<>();
        for(String className : classNames) {
            try {
                classes.add(Class.forName(className));
            } catch(ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return addClassesToRedefine(classes.toArray(new Class[0]));
    }

    /**
     * Add classes to be added to the agent's jar file
     *
     * @param classes The classes to add
     * @return This <code>AgentInfo</code>
     * @since 1.0.0
     */
    public AgentInfo addAgentClasses(Class<?>... classes) {
        this.agentClasses.addAll(Arrays.asList(classes));
        return this;
    }

    /**
     * Add class names to be added to the agent's jar file. These classes will be found through
     * {@link Class#forName(String)}
     *
     * @param classNames The class names to add
     * @return This <code>AgentInfo</code>
     * @since 1.0.0
     */
    public AgentInfo addAgentClasses(String... classNames) {
        List<Class<?>> classes = new ArrayList<>();
        for(String className : classNames) {
            try {
                classes.add(Class.forName(className));
            } catch(ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return addAgentClasses(classes.toArray(new Class[0]));
    }

    /**
     * Get the list of {@link ClassFileTransformer}s of the agent
     *
     * @return The list of {@link ClassFileTransformer}s of the agent
     * @since 1.0.0
     */
    public List<ClassFileTransformer> getTransformers() {
        return transformers;
    }

    /**
     * Get the list of classes to be redefined by this agent
     *
     * @return The list of classes to be redefined by this agent
     * @since 1.0.0
     */
    public List<Class<?>> getClassesToRedefine() {
        return toRedefineClasses;
    }

    /**
     * Get the list of agent's classes to be included in the agent jar
     *
     * @return The list of agent's classes to be included in the agent jar
     * @since 1.0.0
     */
    public List<Class<?>> getAgentClasses() {
        return agentClasses;
    }

    /**
     * Get the Java Virtual Machine process ID to attach the agent to. In 99.9% of cases this is the PID of the current
     * JVM.
     *
     * @return The Java Virtual Machine process ID to attach the agent to
     * @since 1.0.0
     */
    public String getJVMPid() {
        return JVMPid;
    }

    /**
     * Set the Java Virtual Machine process ID to attach the agent to. In 99.9% of cases this is the PID of the current
     * JVM.
     *
     * @param JVMPid The new Java Virtual Machine process ID
     * @return This <code>AgentInfo</code>
     * @since 1.0.0
     */
    public AgentInfo setJVMPid(String JVMPid) {
        this.JVMPid = JVMPid;
        return this;
    }

    /**
     * Get the <code>Class</code> that contains the <code>agentmain()</code> method
     *
     * @return The <code>Class</code> that contains the <code>agentmain()</code> method
     * @since 1.0.0
     */
    public Class<?> getAgentMain() {
        return agentMain;
    }

    /**
     * Set the <code>Class</code> that contains the <code>agentmain()</code> method
     *
     * @param agentMain The new <code>agentmain()</code> class
     * @return This <code>AgentInfo</code>
     * @since 1.0.0
     */
    public AgentInfo setAgentMain(Class<?> agentMain) {
        this.agentMain = agentMain;
        return this;
    }

    /**
     * Get the agent's arguments to be passed to the <code>agentmain()</code> method
     *
     * @return The agent's arguments
     * @since 1.0.0
     */
    public String getAdditionalArgs() {
        return additionalArgs;
    }

    /**
     * Set the agent's arguments to be passed to the <code>agentmain()</code> method
     *
     * @param additionalArgs The new arguments
     * @since 1.0.0
     */
    public void setAdditionalArgs(String additionalArgs) {
        this.additionalArgs = additionalArgs;
    }

    /**
     * Clones this <code>AgentInfo</code>
     *
     * @return A clone of this instance
     * @since 1.0.0
     */
    @Override
    public AgentInfo clone() {
        try {
            AgentInfo clone = (AgentInfo) super.clone();
            clone.transformers = new ArrayList<>(transformers);
            clone.toRedefineClasses = new ArrayList<>(toRedefineClasses);
            clone.agentClasses = new ArrayList<>(agentClasses);
            return clone;
        } catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
