package visitor;


import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.types.Type;

public abstract class Visitor {
	
	public abstract void visitModel(Model e);
	public abstract void visitEntity(Entity e);
	public abstract void visitAttribute(Attribute e);
    public abstract void visitType(Type e);

}
