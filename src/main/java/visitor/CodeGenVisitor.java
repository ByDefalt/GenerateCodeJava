package visitor;

import metaModel.*;
import metaModel.types.*;

public class CodeGenVisitor extends Visitor {

    private final Context ctx = new Context();
    private final CodeGenDelegator modelCodeGenDelegator;
    private final CodeGenDelegator entityCodeGenDelegator;
    private final CodeGenDelegator attributeCodeGenDelegator;
    private final CodeGenDelegator typeCodeGenDelegator;
    private final CodeGenDelegator collectionCodeGenDelegator;


    public CodeGenVisitor(CodeGenDelegator modelCodeGenDelegator, CodeGenDelegator entityCodeGenDelegator, CodeGenDelegator attributeCodeGenDelegator, CodeGenDelegator typeCodeGenDelegator, CodeGenDelegator collectionCodeGenDelegator) {
        this.modelCodeGenDelegator = modelCodeGenDelegator;
        this.entityCodeGenDelegator = entityCodeGenDelegator;
        this.attributeCodeGenDelegator = attributeCodeGenDelegator;
        this.typeCodeGenDelegator = typeCodeGenDelegator;
        this.collectionCodeGenDelegator = collectionCodeGenDelegator;
    }

    public Context getContext() {
        return ctx;
    }

    @Override
    public void visitModel(Model e) {
        modelCodeGenDelegator.delegate(e, this);
    }

    @Override
    public void visitEntity(Entity e) {
        entityCodeGenDelegator.delegate(e, this);
    }

    @Override
    public void visitAttribute(Attribute e) {
        attributeCodeGenDelegator.delegate(e, this);
    }


    @Override
    public void visitSimpleType(SimpleType e) {
    }

    @Override
    public void visitResolvedReference(ResolvedReference e) {
    }

    @Override
    public void visitUnresolvedReference(UnresolvedReference e) {
    }

    @Override
    public void visitArrayType(ArrayType e) {
    }

    @Override
    public void visitListType(ListType e) {
    }

    @Override
    public void visitSetType(SetType e) {
    }

    @Override
    public void visitBagType(BagType e) {
    }

    public String getResult() {
        return ctx.result.toString();
    }

    public Context getCtx() {
        return ctx;
    }

    public CodeGenDelegator getModelDelegator() {
        return modelCodeGenDelegator;
    }

    public CodeGenDelegator getEntityDelegator() {
        return entityCodeGenDelegator;
    }

    public CodeGenDelegator getAttributeDelegator() {
        return attributeCodeGenDelegator;
    }

    public CodeGenDelegator getTypeDelegator() {
        return typeCodeGenDelegator;
    }

    public CodeGenDelegator getCollectionDelegator() {
        return collectionCodeGenDelegator;
    }
}
