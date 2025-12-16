package visitor.java;

import metaModel.Attribute;
import metaModel.Entity;
import visitor.CodeGenDelegator;
import visitor.CodeGenVisitor;
import visitor.Context;
import visitor.Visitor;

public class EntityJavaCodeGenDelegator implements CodeGenDelegator {

    @Override
    public void delegate(Object element, Visitor visitor) {
        CodeGenVisitor v = (CodeGenVisitor) visitor;
        Context ctx = v.getContext();
        Entity e = (Entity) element;

        ctx.fields.setLength(0);
        ctx.methods.setLength(0);

        StringBuilder classBuf = new StringBuilder();
        classBuf.append("public class ").append(e.getName());

        if (e.getSuperEntity() != null) {
            classBuf.append(" extends ").append(e.getSuperEntity().getName());
        }

        classBuf.append(" {\n");

        if (e.getAttributes() != null) {
            for (Attribute a : e.getAttributes()) {
                a.accept(visitor);
            }
        }

        classBuf.append(ctx.fields);
        classBuf.append(ctx.methods);
        classBuf.append("}\n\n");

        ctx.result.append(classBuf);

    }
}
