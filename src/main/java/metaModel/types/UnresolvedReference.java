package metaModel.types;

import visitor.Visitor;

public class UnresolvedReference extends ReferenceType{

    public UnresolvedReference(String entityName) {
        super(entityName);
    }


    @Override
    public void accept(Visitor v) {
        v.visitUnresolvedReference(this);
    }

}
