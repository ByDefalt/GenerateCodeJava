package visitor.java;

import metaModel.types.ArrayType;
import visitor.CodeGenDelegator;
import visitor.CodeGenVisitor;
import visitor.Visitor;

public class ArrayTypeJavaCodeGenDelegator implements CodeGenDelegator {

    @Override
    public void delegate(Object element, Visitor visitor) {
        ArrayType e = (ArrayType) element;
        CodeGenVisitor v = (CodeGenVisitor) visitor;

        e.getElementType().accept(visitor);
        v.getContext().currentType += "[]";
    }
}
