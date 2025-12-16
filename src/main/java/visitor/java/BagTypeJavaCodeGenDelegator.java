package visitor.java;

import metaModel.types.BagType;
import visitor.CodeGenDelegator;
import visitor.CodeGenVisitor;
import visitor.Visitor;

public class BagTypeJavaCodeGenDelegator implements CodeGenDelegator {

    @Override
    public void delegate(Object element, Visitor visitor) {
        BagType e = (BagType) element;
        CodeGenVisitor v = (CodeGenVisitor) visitor;

        e.getElementType().accept(visitor);
        v.getContext().currentType =
                "List<" + v.getContext().currentType + ">";
    }
}
