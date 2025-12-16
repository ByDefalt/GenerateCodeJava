package visitor.java;

import metaModel.types.ResolvedReference;
import visitor.CodeGenDelegator;
import visitor.CodeGenVisitor;
import visitor.Visitor;

public class ResolvedReferenceJavaCodeGenDelegator implements CodeGenDelegator {

    @Override
    public void delegate(Object element, Visitor visitor) {
        ResolvedReference e = (ResolvedReference) element;
        CodeGenVisitor v = (CodeGenVisitor) visitor;
        v.getContext().currentType =
                e.getReferencedEntity().getName();
    }
}
