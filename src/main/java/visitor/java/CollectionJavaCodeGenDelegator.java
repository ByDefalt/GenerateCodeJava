package visitor.java;

import metaModel.Attribute;
import metaModel.types.*;
import visitor.CodeGenDelegator;
import visitor.CodeGenVisitor;
import visitor.Context;
import visitor.Visitor;

public class CollectionJavaCodeGenDelegator implements CodeGenDelegator {

    @Override
    public void delegate(Object element, Visitor visitor) {
        CodeGenVisitor v = (CodeGenVisitor) visitor;
        Context ctx = v.getContext();

        Attribute attr = (Attribute) element;
        CollectionType type = (CollectionType) attr.getType();

        type.getElementType().accept(visitor);
        String base = ctx.currentType;
        String name = attr.getName();
        String cap = Character.toUpperCase(name.charAt(0)) + name.substring(1);

        String singular = cap.endsWith("s") ? cap.substring(0, cap.length() - 1) : cap;
        String impl = (type instanceof SetType) ? "HashSet" : "ArrayList";

        ctx.methods.append("\n    public void add").append(singular)
                .append("(").append(base).append(" item) {\n")
                .append("        if (").append(name).append(" == null) ")
                .append(name).append(" = new ").append(impl).append("<>();\n")
                .append("        ").append(name).append(".add(item);\n")
                .append("    }\n");
    }
}
