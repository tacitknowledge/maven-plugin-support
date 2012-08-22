package com.tacitknowledge.pluginsupport.util;

import org.apache.maven.plugin.logging.Log;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Feb 18, 2007
 * Time: 11:49:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class NullLogger implements Log {
    public boolean isDebugEnabled() {
        return true;
    }

    public void debug(CharSequence charSequence) {

    }

    public void debug(CharSequence charSequence, Throwable throwable) {

    }

    public void debug(Throwable throwable) {

    }

    public boolean isInfoEnabled() {
        return true;
    }

    public void info(CharSequence charSequence) {

    }

    public void info(CharSequence charSequence, Throwable throwable) {

    }

    public void info(Throwable throwable) {

    }

    public boolean isWarnEnabled() {
        return true;
    }

    public void warn(CharSequence charSequence) {

    }

    public void warn(CharSequence charSequence, Throwable throwable) {

    }

    public void warn(Throwable throwable) {

    }

    public boolean isErrorEnabled() {
        return true;   
    }

    public void error(CharSequence charSequence) {

    }

    public void error(CharSequence charSequence, Throwable throwable) {

    }

    public void error(Throwable throwable) {

    }

}
