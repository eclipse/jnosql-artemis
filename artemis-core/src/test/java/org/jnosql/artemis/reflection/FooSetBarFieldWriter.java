package org.jnosql.artemis.reflection;

import org.jnosql.artemis.reflection.FieldWriter;

public class FooSetBarFieldWriter implements FieldWriter {

    @Override
    public void write(Object bean, Object value) {
        Foo.class.cast(bean).setBar((String) value);

    }
}
