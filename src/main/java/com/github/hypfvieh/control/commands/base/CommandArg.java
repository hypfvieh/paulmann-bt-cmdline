package com.github.hypfvieh.control.commands.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import com.github.hypfvieh.control.jline3.ArgWithDescription;

/**
 * Specify arguments for the command line.
 */
public class CommandArg {
    static final List<CommandArg> NO_ARGS = new ArrayList<>();
    
    private final String argName;
    private final boolean required;
    private final boolean generateOnce;
    
    private final Supplier<List<ArgWithDescription>> argumentGenerator;

    private final List<ArgWithDescription> cachedResults = new ArrayList<>();
    
    /**
     * 
     * @param _argName Name of the argument
     * @param _required argument is required
     * @param _generateOnce only call the Supplier method once (cache results) and use the cache on any subsequent call
     * @param _generator supplier method to generate list of possible argument values
     */
    public CommandArg(String _argName, boolean _required, boolean _generateOnce, Supplier<List<ArgWithDescription>> _generator) {
        Objects.requireNonNull(_argName);
        argName = _argName;
        required = _required;
        generateOnce = _generateOnce;
        argumentGenerator = _generator;        
    }
    
    public CommandArg(String _argName, boolean _required) {
        this(_argName, _required, true, null);
    }
    
    /**
     * Name of this argument
     * @return string
     */
    public String getArgName() {
        return argName;
    }

    /**
     * Is this argument required.
     * @return true if required, false otherwise
     */
    public boolean isRequired() {
        return required;
    }
    
    /**
     * Generate argument list once.
     * @return true if argument list is only created once, false otherwise
     */
    public boolean isGenerateOnce() {
        return generateOnce;
    }

    /**
     * Returns a list with all supported argument values.
     * @return List, maybe empty, never null
     */
    public List<ArgWithDescription> getArguments() {
        
        if (argumentGenerator == null) {
            return Collections.unmodifiableList(cachedResults);
        }
        if (isGenerateOnce() && cachedResults.isEmpty()) {
            List<ArgWithDescription> generatedResults = argumentGenerator.get();
            cachedResults.addAll(generatedResults);
            return Collections.unmodifiableList(cachedResults);
        } else {
            return Collections.unmodifiableList(argumentGenerator.get());
        }
    }

    @Override
    public String toString() {
        String format = "%s";
        if (!isRequired()) {
            format = "[%s]";
        } 
        
        return String.format(format, argName);
    }
    
    
}
