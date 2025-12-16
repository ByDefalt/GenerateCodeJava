package metaModel.types;

public abstract class CollectionType extends Type {
    private final Type elementType;
    private final Integer minCardinality;
    private final Integer maxCardinality;

    public CollectionType(Type elementType) {
        this.elementType = elementType;
        this.minCardinality = null;
        this.maxCardinality = null;
    }

    public CollectionType(Type elementType, Integer minCardinality, Integer maxCardinality) {
        this.elementType = elementType;
        this.minCardinality = minCardinality;
        this.maxCardinality = maxCardinality;
    }

    public Type getElementType() {
        return elementType;
    }

    public Integer getMinCardinality() {
        return minCardinality;
    }

    public Integer getMaxCardinality() {
        return maxCardinality;
    }
}