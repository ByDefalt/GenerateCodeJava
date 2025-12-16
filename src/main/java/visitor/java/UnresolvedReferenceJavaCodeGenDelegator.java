package visitor.java;

import metaModel.types.UnresolvedReference;
import visitor.CodeGenDelegator;
import visitor.CodeGenVisitor;
import visitor.Visitor;

public class UnresolvedReferenceJavaCodeGenDelegator implements CodeGenDelegator {

    @Override
    public void delegate(Object element, Visitor visitor) {
        UnresolvedReference e = (UnresolvedReference) element;
        CodeGenVisitor v = (CodeGenVisitor) visitor;
        v.getContext().currentType = e.getEntityName();
    }
}
