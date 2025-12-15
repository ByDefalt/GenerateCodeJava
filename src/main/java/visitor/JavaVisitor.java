package visitor;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.Visitor;

public class JavaVisitor extends Visitor {
    String resultBuffer;
    String methodBuffer;

    @Override
    public void visitModel(Model e) {
        for (Entity ent : e.getEntities()){
            ent.accept(this);
        }
    }

    @Override
    public void visitEntity(Entity e) {
        resultBuffer += "public class " + e.getName() + "{\n";
        for (Attribute a : e.getAttributes()){
            a.accept(this);
        }
    }

    @Override
    public void visitAttribute(Attribute e) {
        resultBuffer += e.getType() + " " + e.getName();

        methodBuffer += "\n\npublic " + e.getType() + "get" + e.getName() + "(){ return " + e.getName() + "; }";
        methodBuffer += "\n\npublic " + "void" + "set" + e.getName() + "("+ e.getType() + " " + e.getName()  +"){ return " + e.getName() + "; }";
    }

    public String getResult(){
        return resultBuffer + methodBuffer + "\n}";
    }

}
