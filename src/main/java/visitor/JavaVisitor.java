package visitor;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.Visitor;

public class JavaVisitor extends Visitor {
    String resultBuffer = "";
    String methodBuffer = "";

    @Override
    public void visitModel(Model e) {
        for (Entity ent : e.getEntities()){
            ent.accept(this);
        }
    }

    @Override
    public void visitEntity(Entity e) {
        resultBuffer += "public class " + e.getName() + " {\n";

        if (e.getAttributes() != null) {
            for (Attribute a : e.getAttributes()){
                a.accept(this);
            }
        }

        resultBuffer += methodBuffer;
        resultBuffer += "\n}\n";

        methodBuffer = "";
    }

    @Override
    public void visitAttribute(Attribute e) {
        resultBuffer += "    private " + e.getType() + " " + e.getName() + ";\n";

        String capitalizedName = e.getName().substring(0, 1).toUpperCase() + e.getName().substring(1);

        methodBuffer += "\n    public " + e.getType() + " get" + capitalizedName + "() {\n";
        methodBuffer += "        return " + e.getName() + ";\n";
        methodBuffer += "    }\n";

        methodBuffer += "\n    public void set" + capitalizedName + "(" + e.getType() + " " + e.getName() + ") {\n";
        methodBuffer += "        this." + e.getName() + " = " + e.getName() + ";\n";
        methodBuffer += "    }\n";
    }

    public String getResult(){
        return resultBuffer;
    }
}