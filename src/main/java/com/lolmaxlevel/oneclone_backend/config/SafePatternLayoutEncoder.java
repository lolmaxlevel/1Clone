package com.lolmaxlevel.oneclone_backend.config;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Custom encoder that sanitizes log messages to prevent CRLF injection attacks
 */
public class SafePatternLayoutEncoder extends PatternLayoutEncoder {

    @Override
    public void start() {
        PatternLayout patternLayout = new SafePatternLayout();
        patternLayout.setContext(context);
        patternLayout.setPattern(getPattern());
        patternLayout.setOutputPatternAsHeader(outputPatternAsHeader);
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }

    /**
     * Custom PatternLayout that sanitizes log messages
     */
    public static class SafePatternLayout extends PatternLayout {

        @Override
        public String doLayout(ILoggingEvent event) {
            String originalMessage = super.doLayout(event);
            return sanitizeLogMessage(originalMessage);
        }

        /**
         * Sanitizes log message to prevent CRLF injection
         * @param message the original log message
         * @return sanitized message with CRLF characters replaced
         */
        private String sanitizeLogMessage(String message) {
            if (message == null) {
                return null;
            }
            return message
                    .replace('\r', '_')
                    .replace('\n', '_')
                    .replace('\t', ' ');
        }
    }
}
