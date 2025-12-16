package visitor.java;

import metaModel.types.ListType;
import visitor.CodeGenDelegator;
import visitor.CodeGenVisitor;
import visitor.Visitor;

public class ListTypeJavaCodeGenDelegator implements CodeGenDelegator {

    @Override
    public void delegate(Object element, Visitor visitor) {
        ListType e = (ListType) element;
        CodeGenVisitor v = (CodeGenVisitor) visitor;

        e.getElementType().accept(visitor);
        v.getContext().currentType =
                "List<" + v.getContext().currentType + ">";
    }
}
