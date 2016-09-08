package org.jnosql.artemis.reflection;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.exception.CloneFailedException;

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
        ClassRepresentation classRepresentation = representations.get(classEntity.getName());
        if (classRepresentation == null) {
            load(classEntity);
            return this.get(classEntity);
        }
        return classRepresentation;
    }

    public ClassRepresentation findByName(String name) {
        return representations.keySet().stream()
                .map(k -> representations.get(k))
                .filter(r -> r.getName().equals(name)).findFirst()
                .orElseThrow(() -> new ClassInformationNotFoundException("There is not entity found with the name: " + name));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("representations", representations)
                .toString();
    }


}
