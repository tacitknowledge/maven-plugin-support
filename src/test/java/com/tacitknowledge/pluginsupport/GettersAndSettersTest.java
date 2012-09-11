/*
 * Copyright 2012 Tacit Knowledge.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tacitknowledge.pluginsupport;

import com.tacitknowledge.pluginsupport.report.AggregateReport;
import com.tacitknowledge.pluginsupport.report.ReportSummary;
import java.awt.print.Book;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
@RunWith(Parameterized.class)
public class GettersAndSettersTest {

    private static final List<Class[]> TEST_CLASSES = Arrays.asList(new Class[][]
            {{AggregateReport.class}, {ReportSummary.class}});

    @Parameterized.Parameters
    public static List<Class[]> getClasses() {
        return TEST_CLASSES;
    }

    private PropertyDescriptor[] propertyDescriptors;

    private Object object;

    public GettersAndSettersTest(Class testClass) throws Exception {
        this.propertyDescriptors = Introspector.getBeanInfo(testClass).getPropertyDescriptors();
        this.object = Mockito.mock(testClass, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        for(PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (hasGettersAndSetters(propertyDescriptor)) {
                Object mockedValue = createValue(propertyDescriptor.getPropertyType());
                propertyDescriptor.getWriteMethod().invoke(object, mockedValue);
                Object result = propertyDescriptor.getReadMethod().invoke(object);

                if (propertyDescriptor.getPropertyType().isPrimitive()) {
                    Assert.assertEquals(mockedValue, result);
                } else {
                    Assert.assertSame(mockedValue, result);
                }
            }
        }
    }

    private boolean hasGettersAndSetters(PropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null;
    }

    private Object createValue(Class type) throws Exception {
        if (type.isPrimitive()) {
            if (Integer.TYPE.equals(type) || Short.TYPE.equals(type) || Long.TYPE.equals(type) || Byte.TYPE.equals(type)) {
                return 105;
            } else if (Boolean.TYPE.equals(type)) {
                return true;
            } else if (Character.TYPE.equals(type)) {
                return 'f';
            }
        }
        try {
            Constructor constructor = type.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException ex) {
            return Mockito.mock(type);
        }
    }

}
