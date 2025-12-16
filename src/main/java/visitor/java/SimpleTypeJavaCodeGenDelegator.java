package visitor.java;

import metaModel.types.SimpleType;
import visitor.CodeGenDelegator;
import visitor.CodeGenVisitor;
import visitor.Visitor;

public class SimpleTypeJavaCodeGenDelegator implements CodeGenDelegator {

    @Override
    public void delegate(Object element, Visitor visitor) {
        SimpleType e = (SimpleType) element;
        CodeGenVisitor v = (CodeGenVisitor) visitor;
        v.getContext().currentType = e.getTypeName();
    }
}
