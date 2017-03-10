package org.jnosql.artemis.key;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;


public class DefaultEntityKeyValuePrePersist implements EntityKeyValuePrePersist{


    private final Object value;

    DefaultEntityKeyValuePrePersist(Object value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultEntityKeyValuePrePersist)) {
            return false;
        }
        DefaultEntityKeyValuePrePersist that = (DefaultEntityKeyValuePrePersist) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("value", value)
                .toString();
    }
}
