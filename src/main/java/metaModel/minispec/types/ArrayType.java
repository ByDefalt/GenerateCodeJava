package metaModel.minispec.types;

import visitor.Visitor;

public class ArrayType extends CollectionType {
    private final Integer size;

    public ArrayType(Type elementType, Integer size) {
        super(elementType, 0, size-1);
        this.size = size;
    }

    public Integer getSize() {
        return size;
    }

    @Override
    public void accept(Visitor v) {
        v.visitArrayType(this);
    }

}