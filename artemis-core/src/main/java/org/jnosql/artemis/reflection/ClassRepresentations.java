package org.jnosql.artemis.reflection;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@ApplicationScoped
public class ClassRepresentations {

    private static final Map<String, ClassRepresentation> REPRESENTATIONS = new ConcurrentHashMap<>();


    private ClassConverter classConverter;

    @Inject
    ClassRepresentations(ClassConverter classConverter) {
        this.classConverter = classConverter;
    }

    ClassRepresentations() {
    }

    void load(Class classEntity) {
        ClassRepresentation classRepresentation = classConverter.create(classEntity);
        REPRESENTATIONS.put(classEntity.getName(), classRepresentation);
    }

    public ClassRepresentation get(Class classEntity) {
        ClassRepresentation classRepresentation = REPRESENTATIONS.get(classEntity.getName());
        if (classRepresentation == null) {
            load(classEntity);
            return this.get(classEntity);
        }
        return classRepresentation;
    }

    public ClassRepresentation findByName(String name) {
        return REPRESENTATIONS.keySet().stream()
                .map(k -> REPRESENTATIONS.get(k))
                .filter(r -> r.getName().equalsIgnoreCase(name)).findFirst()
                .orElseThrow(() -> new ClassInformationNotFoundException("There is not entity found with the name: " + name));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("REPRESENTATIONS", REPRESENTATIONS)
                .toString();
    }


}
