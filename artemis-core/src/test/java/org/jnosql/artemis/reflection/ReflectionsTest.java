package org.jnosql.artemis.reflection;

import javax.inject.Inject;
import org.jnosql.artemis.WeldJUnit4Runner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;


@RunWith(WeldJUnit4Runner.class)
public class ReflectionsTest {


    @Inject
    private Reflections reflections;

    @Test
    public void init() {
        System.out.println(reflections);
    }

}