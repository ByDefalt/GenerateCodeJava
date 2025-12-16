package metaModel;

import metaModel.types.Type;
import visitor.Visitor;

public class Attribute implements MinispecElement {
    private final String name;
    private final Type type;


    public Attribute(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }


    @Override
    public void accept(Visitor v) {
        v.visitAttribute(this);
    }
}