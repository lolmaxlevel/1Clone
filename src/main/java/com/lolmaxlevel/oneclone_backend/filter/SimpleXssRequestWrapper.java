package com.lolmaxlevel.oneclone_backend.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

@Slf4j
public class SimpleXssRequestWrapper extends HttpServletRequestWrapper {

    private final String cleanedBody;

    public SimpleXssRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            // Читаем и экранируем JSON данные
            String requestBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            this.cleanedBody = escapeJsonContent(requestBody);
        } else {
            this.cleanedBody = null;
        }
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }

        String[] escapedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            escapedValues[i] = escapeHtml(values[i]);
        }
        return escapedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return escapeHtml(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return escapeHtml(value);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (cleanedBody == null) {
            return super.getInputStream();
        }

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
            cleanedBody.getBytes(StandardCharsets.UTF_8)
        );

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // Not implemented
            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(value);
    }

    private String escapeJsonContent(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return jsonString;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonString);

            if (rootNode.isObject()) {
                escapeJsonNode((ObjectNode) rootNode);
            }

            return mapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            log.warn("Failed to parse JSON for escaping, returning original: {}", e.getMessage());
            return jsonString;
        }
    }

    private void escapeJsonNode(ObjectNode node) {
        Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode fieldValue = node.get(fieldName);

            if (fieldValue.isTextual()) {
                String escapedValue = escapeHtml(fieldValue.asText());
                node.put(fieldName, escapedValue);
            } else if (fieldValue.isObject()) {
                escapeJsonNode((ObjectNode) fieldValue);
            }
        }
    }
}
