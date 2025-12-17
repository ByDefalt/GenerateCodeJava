package visitor.java;

import metaModel.minispec.Entity;
import metaModel.minispec.Model;
import visitor.CodeGenDelegator;
import visitor.Visitor;

public class ModelJavaCodeGenDelegator implements CodeGenDelegator {

    @Override
    public void delegate(Object element, Visitor visitor) {
        Model model = (Model) element;
        if (model.getEntities() != null) {
            for (Entity e : model.getEntities()) {
                e.accept(visitor);
            }
        }
    }
}
