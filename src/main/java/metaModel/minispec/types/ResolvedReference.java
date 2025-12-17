package metaModel.minispec.types;

import metaModel.minispec.Entity;
import visitor.Visitor;

public class ResolvedReference extends ReferenceType{

    private final Entity referencedEntity;

    public ResolvedReference(String entityName, Entity referencedEntity) {
        super(entityName);
        this.referencedEntity = referencedEntity;
    }

    public Entity getReferencedEntity() {
        return referencedEntity;
    }

    @Override
    public void accept(Visitor v) {
        v.visitResolvedReference(this);
    }
}
