package visitor.java;

import metaModel.types.SetType;
import visitor.CodeGenDelegator;
import visitor.CodeGenVisitor;
import visitor.Visitor;

public class SetTypeJavaCodeGenDelegator implements CodeGenDelegator {

    @Override
    public void delegate(Object element, Visitor visitor) {
        SetType e = (SetType) element;
        CodeGenVisitor v = (CodeGenVisitor) visitor;

        e.getElementType().accept(visitor);
        v.getContext().currentType =
                "Set<" + v.getContext().currentType + ">";
    }
}
