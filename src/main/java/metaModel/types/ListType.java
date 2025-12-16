package metaModel.types;

import visitor.Visitor;

public class ListType extends CollectionType {

    public ListType(Type elementType) {
        super(elementType);
    }

    public ListType(Type elementType, Integer minCardinality, Integer maxCardinality) {
        super(elementType, minCardinality, maxCardinality);
    }

    @Override
    public void accept(Visitor v) {
        v.visitListType(this);
    }
}