package visitor.java;

import metaModel.Entity;
import metaModel.Model;
import visitor.Delegator;
import visitor.Visitor;

public class ModelDelegator implements Delegator {
    @Override
    public void delegate(Object element, Visitor visitor) {
        Model e = (Model) element;
        if (e.getEntities() != null) {
            for (Entity ent : e.getEntities()) {
                ent.accept(visitor);
            }
        }
    }
}
