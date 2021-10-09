package de.nb.aventiure2.logger;

import android.util.Log;

/**
 * Logger. Thanks to Eefret and Thomas Vos, see https://stackoverflow.com/a/26109973
 */
public class Logger {

    public enum LOGGER_DEPTH {
        ACTUAL_METHOD(4),
        LOGGER_METHOD(3),
        STACK_TRACE_METHOD(1),
        JVM_METHOD(0);

        private final int value;

        LOGGER_DEPTH(final int newValue) {
            value = newValue;
        }

        int getValue() {
            return value;
        }
    }

    private static final String personalTAG = "Logger";

    private final StringBuilder sb;

    private Logger() {
        if (LoggerLoader.instance != null) {
            Log.e(personalTAG, "Error: Logger already instantiated");
            throw new IllegalStateException("Already Instantiated");
        } else {
            sb = new StringBuilder(255);
        }
    }

    public static Logger getLogger() {
        return LoggerLoader.instance;
    }

    private String getTag(final LOGGER_DEPTH depth) {
        try {
            final String className =
                    Thread.currentThread().getStackTrace()[depth.getValue()].getClassName();
            sb.append(className.substring(className.lastIndexOf(".") + 1));
            sb.append("[");
            sb.append(Thread.currentThread().getStackTrace()[depth.getValue()].getMethodName());
            sb.append("]");
            // sb.append(" - ");
            // sb.append(Thread.currentThread().getStackTrace()[depth.getValue()].getLineNumber());
            return sb.toString();
        } catch (final Exception ex) {
            ex.printStackTrace();
            Log.d(personalTAG, ex.getMessage());
        } finally {
            sb.setLength(0);
        }
        return null;
    }

    public void d(final String msg) {
        try {
            Log.d(getTag(LOGGER_DEPTH.ACTUAL_METHOD), msg);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    public void d(final String msg, final LOGGER_DEPTH depth) {
        try {
            Log.d(getTag(depth), msg);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    public void d(final String msg, final Throwable t, final LOGGER_DEPTH depth) {
        try {
            Log.d(getTag(depth), msg, t);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    public void e(final String msg) {
        try {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD), msg);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    public void e(final String msg, final LOGGER_DEPTH depth) {
        try {
            Log.e(getTag(depth), msg);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    public void e(final String msg, final Throwable t, final LOGGER_DEPTH depth) {
        try {
            Log.e(getTag(depth), msg, t);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    public void w(final String msg) {
        try {
            Log.w(getTag(LOGGER_DEPTH.ACTUAL_METHOD), msg);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    public void w(final String msg, final LOGGER_DEPTH depth) {
        try {
            Log.w(getTag(depth), msg);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    public void w(final String msg, final Throwable t, final LOGGER_DEPTH depth) {
        try {
            Log.w(getTag(depth), msg, t);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    public void v(final String msg) {
        try {
            Log.v(getTag(LOGGER_DEPTH.ACTUAL_METHOD), msg);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    public void v(final String msg, final LOGGER_DEPTH depth) {
        try {
            Log.v(getTag(depth), msg);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    public void v(final String msg, final Throwable t, final LOGGER_DEPTH depth) {
        try {
            Log.v(getTag(depth), msg, t);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    public void i(final String msg) {
        try {
            Log.i(getTag(LOGGER_DEPTH.ACTUAL_METHOD), msg);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    public void i(final String msg, final LOGGER_DEPTH depth) {
        try {
            Log.i(getTag(depth), msg);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    public void i(final String msg, final Throwable t, final LOGGER_DEPTH depth) {
        try {
            Log.i(getTag(depth), msg, t);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void wtf(final String msg) {
        try {
            Log.wtf(getTag(LOGGER_DEPTH.ACTUAL_METHOD), msg);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    public void wtf(final String msg, final LOGGER_DEPTH depth) {
        try {
            Log.wtf(getTag(depth), msg);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    public void wtf(final String msg, final Throwable t, final LOGGER_DEPTH depth) {
        try {
            Log.wtf(getTag(depth), msg, t);
        } catch (final Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD),
                    "Logger failed, exception: " + exception.getMessage());
        }
    }

    private static class LoggerLoader {
        private static final Logger instance = new Logger();
    }
}