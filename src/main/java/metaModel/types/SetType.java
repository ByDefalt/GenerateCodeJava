package metaModel.types;

import visitor.Visitor;

public class SetType extends CollectionType {

    public SetType(Type elementType) {
        super(elementType);
    }

    public SetType(Type elementType, Integer minCardinality, Integer maxCardinality) {
        super(elementType, minCardinality, maxCardinality);
    }

    @Override
    public void accept(Visitor v) {
        v.visitSetType(this);
    }
}