package metaModel.minispec.types;

import visitor.Visitor;

public class BagType extends CollectionType {

    public BagType(Type elementType) {
        super(elementType);
    }

    public BagType(Type elementType, Integer minCardinality, Integer maxCardinality) {
        super(elementType, minCardinality, maxCardinality);
    }

    @Override
    public void accept(Visitor v) {
        v.visitBagType(this);
    }
}