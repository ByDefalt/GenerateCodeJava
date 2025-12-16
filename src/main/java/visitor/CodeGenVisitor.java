package visitor;

import metaModel.*;
import metaModel.types.*;
import visitor.java.*;

public class CodeGenVisitor extends Visitor {

    private final Context ctx = new Context();
    private final CodeGenDelegator modelCodeGenDelegator;
    private final CodeGenDelegator entityCodeGenDelegator;
    private final CodeGenDelegator attributeCodeGenDelegator;
    private final CodeGenDelegator typeCodeGenDelegator;
    private final CodeGenDelegator collectionCodeGenDelegator;
    private final CodeGenDelegator simpleTypeCodeGenDelegator;
    private final CodeGenDelegator resolvedRefCodeGenDelegator;
    private final CodeGenDelegator unresolvedRefCodeGenDelegator;
    private final CodeGenDelegator arrayTypeCodeGenDelegator;
    private final CodeGenDelegator listTypeCodeGenDelegator;
    private final CodeGenDelegator setTypeCodeGenDelegator;
    private final CodeGenDelegator bagTypeCodeGenDelegator;


    public CodeGenVisitor(CodeGenDelegator modelCodeGenDelegator, CodeGenDelegator entityCodeGenDelegator, CodeGenDelegator attributeCodeGenDelegator, CodeGenDelegator typeCodeGenDelegator, CodeGenDelegator collectionCodeGenDelegator, CodeGenDelegator simpleTypeCodeGenDelegator, CodeGenDelegator resolvedRefCodeGenDelegator, CodeGenDelegator unresolvedRefCodeGenDelegator, CodeGenDelegator arrayTypeCodeGenDelegator, CodeGenDelegator listTypeCodeGenDelegator, CodeGenDelegator setTypeCodeGenDelegator, CodeGenDelegator bagTypeCodeGenDelegator) {
        this.modelCodeGenDelegator = modelCodeGenDelegator;
        this.entityCodeGenDelegator = entityCodeGenDelegator;
        this.attributeCodeGenDelegator = attributeCodeGenDelegator;
        this.typeCodeGenDelegator = typeCodeGenDelegator;
        this.collectionCodeGenDelegator = collectionCodeGenDelegator;
        this.simpleTypeCodeGenDelegator = simpleTypeCodeGenDelegator;
        this.resolvedRefCodeGenDelegator = resolvedRefCodeGenDelegator;
        this.unresolvedRefCodeGenDelegator = unresolvedRefCodeGenDelegator;
        this.arrayTypeCodeGenDelegator = arrayTypeCodeGenDelegator;
        this.listTypeCodeGenDelegator = listTypeCodeGenDelegator;
        this.setTypeCodeGenDelegator = setTypeCodeGenDelegator;
        this.bagTypeCodeGenDelegator = bagTypeCodeGenDelegator;
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
        simpleTypeCodeGenDelegator.delegate(e, this);
    }

    @Override
    public void visitResolvedReference(ResolvedReference e) {
        resolvedRefCodeGenDelegator.delegate(e, this);
    }

    @Override
    public void visitUnresolvedReference(UnresolvedReference e) {
        unresolvedRefCodeGenDelegator.delegate(e, this);
    }

    @Override
    public void visitArrayType(ArrayType e) {
        arrayTypeCodeGenDelegator.delegate(e, this);
    }

    @Override
    public void visitListType(ListType e) {
        listTypeCodeGenDelegator.delegate(e, this);
    }

    @Override
    public void visitSetType(SetType e) {
        setTypeCodeGenDelegator.delegate(e, this);
    }

    @Override
    public void visitBagType(BagType e) {
        bagTypeCodeGenDelegator.delegate(e, this);
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

    public CodeGenDelegator getSimpleTypeDelegator() {
        return simpleTypeCodeGenDelegator;
    }

    public CodeGenDelegator getResolvedRefDelegator() {
        return resolvedRefCodeGenDelegator;
    }

    public CodeGenDelegator getUnresolvedRefDelegator() {
        return unresolvedRefCodeGenDelegator;
    }

    public CodeGenDelegator getArrayTypeDelegator() {
        return arrayTypeCodeGenDelegator;
    }

    public CodeGenDelegator getListTypeDelegator() {
        return listTypeCodeGenDelegator;
    }

    public CodeGenDelegator getSetTypeDelegator() {
        return setTypeCodeGenDelegator;
    }

    public CodeGenDelegator getBagTypeDelegator() {
        return bagTypeCodeGenDelegator;
    }
}
