package io.mateu.erp.model.beroni;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by miguel on 1/6/17.
 */
public class BeroniRecord {

    @Override
    public String toString() {
        String l = getClass().getName().substring(getClass().getName().length() - 1);

        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(getClass()).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName()))
                    append(l, pd.getReadMethod().invoke(this));
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return super.toString();
    }

    private void append(String l, Object s) {
        l += "#";
        if (s != null) l += s;
    }

    private void append(String l, String s) {
        l += "#";
        if (s != null) l += s;
    }

    private void append(String l, char s) {
        l += "#";
        l += s;
    }

    private void append(String l, long s) {
        l += "#";
        l += s;
    }

    private void append(String l, int s) {
        l += "#";
        l += s;
    }

    private void append(String l, double s) {
        l += "#";
        l += new DecimalFormat("#.00").format(s);
    }

    private void append(String l, LocalDate s) {
        l += "#";
        if (s != null) l += s.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

}
