package metaModel.minispec.types;

import visitor.Visitor;

public class SimpleType extends Type {
    private final String typeName;

    public SimpleType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public void accept(Visitor v) {
        v.visitSimpleType(this);
    }

}