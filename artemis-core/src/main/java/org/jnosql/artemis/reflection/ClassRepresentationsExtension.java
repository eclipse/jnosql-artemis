package org.jnosql.artemis.reflection;


import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import org.jnosql.artemis.Entity;

@ApplicationScoped
public class ClassRepresentationsExtension implements Extension {

    private static final Logger LOGGER = Logger.getLogger(ClassRepresentationsExtension.class.getName());


    private static final ClassRepresentations CLASS_REPRESENTATIONS;


    static {
        ClassConverter classConverter = new ClassConverter(new Reflections());
        CLASS_REPRESENTATIONS = new ClassRepresentations(classConverter);
    }



    public <T> void initializePropertyLoading(final @Observes ProcessAnnotatedType<T> target) {

        AnnotatedType<T> at = target.getAnnotatedType();
        if(!at.isAnnotationPresent(Entity.class)) {
            return;
        }
        Class<T> javaClass = target.getAnnotatedType().getJavaClass();
        LOGGER.info("scanning type: "  + javaClass.getName());
        CLASS_REPRESENTATIONS.load(javaClass);
    }

}
