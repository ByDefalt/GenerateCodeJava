package visitor;

import metaModel.*;
import metaModel.types.*;

public abstract class Visitor {

	// Éléments principaux du métamodèle
	public abstract void visitModel(Model e);
	public abstract void visitEntity(Entity e);
	public abstract void visitAttribute(Attribute e);

	// Hiérarchie des types
	public abstract void visitSimpleType(SimpleType e);
	public abstract void visitResolvedReference(ResolvedReference e);
	public abstract void visitUnresolvedReference(UnresolvedReference e);
	public abstract void visitArrayType(ArrayType e);
	public abstract void visitListType(ListType e);
	public abstract void visitSetType(SetType e);
	public abstract void visitBagType(BagType e);
}