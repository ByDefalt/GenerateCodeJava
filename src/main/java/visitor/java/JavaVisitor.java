package visitor.java;

import metaModel.configMetaModel.java.JavaMetaModelConfiguration;
import metaModel.minispec.types.*;
import visitor.CodeGenVisitor;

public class JavaVisitor extends CodeGenVisitor {

    public JavaVisitor() {
        super(
                new ModelJavaCodeGenDelegator(),
                new EntityJavaCodeGenDelegator(),
                new AttributeJavaCodeGenDelegator(),
                null,
                new CollectionJavaCodeGenDelegator(),
                new JavaMetaModelConfiguration()
        );
    }

    // --- Types simples et références ---

    @Override
    public void visitSimpleType(SimpleType e) {
        getContext().currentType = e.getTypeName();
    }

    @Override
    public void visitResolvedReference(ResolvedReference e) {
        getContext().currentType = e.getReferencedEntity().getName();
    }

    @Override
    public void visitUnresolvedReference(UnresolvedReference e) {
        getContext().currentType = e.getEntityName();
    }

    // --- Collections et arrays ---

    @Override
    public void visitArrayType(ArrayType e) {
        e.getElementType().accept(this);
        getContext().currentType += "[]";
    }

    @Override
    public void visitListType(ListType e) {
        e.getElementType().accept(this);
        getContext().currentType = "List<" + getContext().currentType + ">";
    }

    @Override
    public void visitSetType(SetType e) {
        e.getElementType().accept(this);
        getContext().currentType = "Set<" + getContext().currentType + ">";
    }

    @Override
    public void visitBagType(BagType e) {
        e.getElementType().accept(this);
        getContext().currentType = "List<" + getContext().currentType + ">";
    }
}
