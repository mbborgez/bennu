package org.fenixedu.bennu.portal.servlet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.io.BaseEncoding;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;

public class PortalExtension extends AbstractExtension {

    @Override
    public Map<String, Filter> getFilters() {
        Map<String, Filter> filters = new HashMap<>();
        filters.put("base64", new Base64Filter());
        return filters;
    }

    private static class Base64Filter implements Filter {

        @Override
        public List<String> getArgumentNames() {
            return null;
        }

        @Override
        public Object apply(Object input, Map<String, Object> args) {
            if (input instanceof byte[]) {
                byte[] bytes = (byte[]) input;
                return BaseEncoding.base64().encode(bytes);
            }
            return input;
        }

    }

}
