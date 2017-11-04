![Eclipse JNoSQL Artemis Project](https://github.com/JNOSQL/diana-site/blob/master/images/duke-artemis.png)
# Eclipse JNoSQL Artemis Core


The Eclipse JNoSQL Artemis Core is the commons project in the mapping project. It has the commons annotations.

## Entities Mapping

* `@Entity`: Specifies that the class is an entity. This annotation is applied to the entity class.
* `@Column`: Specifies a mapped column for a persistent property or field.
* `@Id`: Defines the field is the key of the Entity
* `@Embeddable`:Defines a class whose instances are stored as an intrinsic part of an owning entity and share the identity of the entity.
* `@MappedSuperclass`: Designates a class whose mapping information is applied to the entities that inherit from it. A mapped superclass has no separate table defined for it.

E.g.:

´´´java
@Entity
public class Person {

    @Id
    private long id;

    @Column
    private String name;

    @Column
    private int age;

    @Column
    private List<String> phones;
}    
´´´
