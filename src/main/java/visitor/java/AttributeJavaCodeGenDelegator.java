package visitor.java;

import metaModel.Attribute;
import metaModel.types.CollectionType;
import visitor.CodeGenDelegator;
import visitor.CodeGenVisitor;
import visitor.Context;
import visitor.Visitor;

public class AttributeJavaCodeGenDelegator implements CodeGenDelegator {

    @Override
    public void delegate(Object element, Visitor visitor) {
        CodeGenVisitor v = (CodeGenVisitor) visitor;
        Context ctx = v.getContext();
        Attribute attr = (Attribute) element;

        attr.getType().accept(visitor);
        String type = ctx.currentType;
        String name = attr.getName();
        String cap = Character.toUpperCase(name.charAt(0)) + name.substring(1);

        ctx.fields.append("    private ")
                .append(type)
                .append(" ")
                .append(name)
                .append(";\n");


        ctx.methods.append("\n    public ").append(type).append(" get").append(cap).append("() {\n")
                .append("        return ").append(name).append(";\n")
                .append("    }\n");

        ctx.methods.append("\n    public void set").append(cap)
                .append("(").append(type).append(" ").append(name).append(") {\n")
                .append("        this.").append(name).append(" = ").append(name).append(";\n")
                .append("    }\n");

        if (attr.getType() instanceof CollectionType) {
            v.getCollectionDelegator().delegate(attr, visitor);
        }
    }
}
