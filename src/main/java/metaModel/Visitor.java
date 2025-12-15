package metaModel;


public abstract class Visitor {
	
	public abstract void visitModel(Model e);
	public abstract void visitEntity(Entity e);
	public abstract void visitAttribute(Attribute e);

}
