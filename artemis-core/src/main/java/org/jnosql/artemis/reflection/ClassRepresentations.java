package org.jnosql.artemis.reflection;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@ApplicationScoped
public class ClassRepresentations {

    private Map<String, ClassRepresentation> representations;

    @Inject
    private ClassConverter classConverter;

    @PostConstruct
    public void init() {
        representations = new ConcurrentHashMap<>();
    }


    public void load(Class classEntity) {
        ClassRepresentation classRepresentation = classConverter.create(classEntity);
        representations.put(classEntity.getName(), classRepresentation);
    }

    public ClassRepresentation get(Class classEntity) {
        return representations.get(classEntity.getName());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("representations", representations)
                .toString();
    }
}
