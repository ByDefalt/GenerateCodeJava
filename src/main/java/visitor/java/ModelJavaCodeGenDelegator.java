package visitor.java;

import metaModel.minispec.Entity;
import metaModel.minispec.Model;
import visitor.CodeGenDelegator;
import visitor.CodeGenVisitor;
import visitor.Context;
import visitor.Visitor;

public class ModelJavaCodeGenDelegator implements CodeGenDelegator {

    @Override
    public void delegate(Object element, Visitor visitor) {
        Model model = (Model) element;
        CodeGenVisitor v = (CodeGenVisitor) visitor;
        Context ctx = v.getContext();
        ctx.packageName.append("package ").append(model.getName()).append(";\n\n");
        if (model.getEntities() != null) {
            for (Entity e : model.getEntities()) {
                e.accept(visitor);
            }
        }
    }
}
