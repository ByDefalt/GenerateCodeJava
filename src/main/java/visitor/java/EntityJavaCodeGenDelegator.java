package visitor.java;

import metaModel.configMetaModel.java.ModelConfig;
import metaModel.minispec.Attribute;
import metaModel.minispec.Entity;
import visitor.CodeGenDelegator;
import visitor.CodeGenVisitor;
import visitor.Context;
import visitor.Visitor;

import java.util.List;

public class EntityJavaCodeGenDelegator implements CodeGenDelegator {

    @Override
    public void delegate(Object element, Visitor visitor) {
        CodeGenVisitor v = (CodeGenVisitor) visitor;
        Context ctx = v.getContext();
        Entity e = (Entity) element;

        ctx.fields.setLength(0);
        ctx.methods.setLength(0);
        ctx.imports.setLength(0);

        StringBuilder classBuf = new StringBuilder();


        classBuf.append("\npublic class ").append(e.getName());

        if (e.getSuperEntity() != null) {
            classBuf.append(" extends ").append(e.getSuperEntity().getName());
        }
        List<ModelConfig> modelConfigList = ((JavaMetaModelConfiguration) ((CodeGenVisitor) visitor).getMetaModelConfiguration()).getModelConfigs();
        if (e.getSuperEntity() != null) {
            String superName = e.getSuperEntity().getName();
            if (superName != null) {
                for (ModelConfig modelConfig : modelConfigList) {
                    if (superName.contains(modelConfig.getName())) {
                        String pkg = modelConfig.getPackageName();
                        if (pkg != null && !pkg.isEmpty()) {
                            String importStmt = "import " + pkg + "." + modelConfig.getName() + ";" + System.lineSeparator();
                            if (ctx.imports.indexOf(importStmt) == -1) {
                                ctx.imports.append(importStmt);
                            }
                        }
                    }
                }
            }
        }


        if (e.getAttributes() != null) {
            for (Attribute a : e.getAttributes()) {
                a.accept(visitor);
            }
        }
        boolean allreadyImplemented = false;
        for(Attribute a : e.getAttributes()){
            if(a.getInitialValue()!=null && a.getInitialValue().contains("(")) {
                if(allreadyImplemented) {
                    classBuf.append(" , ").append(a.getInitialValue().split("\\(")[0]).append("Methods ");
                }else{
                    classBuf.append(" implements ").append(a.getInitialValue().split("\\(")[0]).append("Methods ");
                    allreadyImplemented=true;
                }
            }
        }

        classBuf.append(" {\n");
        classBuf.append(ctx.fields);
        classBuf.append(ctx.methods);
        classBuf.append("}\n\n");

        ctx.result.append(ctx.packageName);
        ctx.result.append(ctx.imports);
        ctx.result.append(classBuf);

    }
}
