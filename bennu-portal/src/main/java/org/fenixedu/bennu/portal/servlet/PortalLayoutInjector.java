package org.fenixedu.bennu.portal.servlet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.commons.i18n.I18N;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

@WebFilter("/*")
public class PortalLayoutInjector implements Filter {

    private PebbleEngine engine;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        engine = new PebbleEngine(new ClasspathLoader() {
            @Override
            public Reader getReader(String templateName) throws LoaderException {
                return new InputStreamReader(filterConfig.getServletContext().getResourceAsStream("/themes/" + templateName));
            }
        });
        if (CoreConfiguration.getConfiguration().developmentMode()) {
            Cache<String, PebbleTemplate> cache = CacheBuilder.newBuilder().maximumSize(0).build();
            engine.setTemplateCache(cache);
        }
        engine.addExtension(new PortalExtension());
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // Wrap the response so it may be later rewritten if necessary
        PortalResponseWrapper wrapper = new PortalResponseWrapper(response);

        chain.doFilter(request, wrapper);

        MenuFunctionality functionality = BennuPortalDispatcher.getSelectedFunctionality(request);
        if (functionality != null) {
            PortalBackend backend = PortalBackendRegistry.getPortalBackend(functionality.getProvider());
            if (backend.requiresServerSideLayout()) {
                String body = wrapper.getContent();
                try {
                    PortalConfiguration config = PortalConfiguration.getInstance();
                    Map<String, Object> ctx = new HashMap<>();
                    ctx.put("loggedUser", Authenticate.getUser());
                    ctx.put("body", body);
                    ctx.put("functionality", functionality);
                    ctx.put("config", config);
                    ctx.put("contextPath", request.getContextPath());
                    ctx.put("devMode", CoreConfiguration.getConfiguration().developmentMode());
                    ctx.put("selectedTopLevel", functionality.getPathFromRoot().get(0));
                    PebbleTemplate template = engine.compile(config.getTheme() + "/" + functionality.resolveLayout() + ".html");
                    template.evaluate(response.getWriter(), ctx, I18N.getLocale());
                } catch (PebbleException e) {
                    e.printStackTrace();
                }
            }
        } else {
            wrapper.flushBuffer();
        }
    }

    @Override
    public void destroy() {

    }

}