package org.fenixedu.bennu.portal.servlet;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class PortalResponseWrapper extends HttpServletResponseWrapper {
    protected CharArrayWriter writer = null;

    public PortalResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new CharArrayWriter();
            getResponse().getWriter();
        }
        return new PrintWriter(writer);
    }

    public String getContent() {
        return writer.toString();
    }

    @Override
    public void resetBuffer() {
        super.resetBuffer();
        if (writer != null) {
            writer.reset();
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        super.flushBuffer();
        if (writer != null) {
            getResponse().getWriter().write(writer.toString());
            writer.reset();
        }
    }

    public boolean hasData() {
        return writer != null;
    }

}