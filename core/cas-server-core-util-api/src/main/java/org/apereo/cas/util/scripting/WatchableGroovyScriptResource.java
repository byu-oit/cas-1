package org.apereo.cas.util.scripting;

import org.apereo.cas.util.ResourceUtils;
import org.apereo.cas.util.io.FileWatcherService;

import groovy.lang.GroovyObject;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

/**
 * This is {@link WatchableGroovyScriptResource}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@Slf4j
@Getter
public class WatchableGroovyScriptResource {
    private transient FileWatcherService watcherService;
    private transient GroovyObject groovyScript;
    private final transient Resource resource;

    @SneakyThrows
    public WatchableGroovyScriptResource(final Resource script) {
        this.resource = script;

        if (ResourceUtils.doesResourceExist(script)) {
            this.watcherService = new FileWatcherService(script.getFile(), file -> {
                try {
                    LOGGER.debug("Reloading script at [{}]", file);
                    compileScriptResource(script);
                } catch (final Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            });
            this.watcherService.start(script.getFilename());
            compileScriptResource(script);
        }
    }

    private void compileScriptResource(final Resource script) {
        this.groovyScript = ScriptingUtils.parseGroovyScript(script, true);
    }

    /**
     * Execute.
     *
     * @param <T>   the type parameter
     * @param args  the args
     * @param clazz the clazz
     * @return the result
     */
    public <T> T execute(final Object[] args, final Class<T> clazz) {
        if (this.groovyScript != null) {
            return ScriptingUtils.executeGroovyScript(this.groovyScript, args, clazz, true);
        }
        return null;
    }

    /**
     * Execute t.
     *
     * @param <T>        the type parameter
     * @param methodName the method name
     * @param clazz      the clazz
     * @param args       the args
     * @return the t
     */
    public <T> T execute(final String methodName, final Class<T> clazz, final Object... args) {
        if (this.groovyScript != null) {
            return ScriptingUtils.executeGroovyScript(this.groovyScript, methodName, args, clazz, true);
        }
        return null;
    }
}
