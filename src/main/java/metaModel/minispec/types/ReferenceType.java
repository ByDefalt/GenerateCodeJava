package metaModel.minispec.types;

public abstract class ReferenceType extends Type {
    private final String entityName;

    public ReferenceType(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }
}