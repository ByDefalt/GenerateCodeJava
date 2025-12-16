package metaModel;

import metaModel.types.Type;
import visitor.Visitor;

public class Attribute implements MinispecElement {
    private String name;
    private Type type;

    public Attribute(String name, String typeStr) {
        this.name = name;
        this.type = new Type(typeStr);
    }

    public Attribute(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setType(String typeStr) {
        this.type = new Type(typeStr);
    }

    @Override
    public void accept(Visitor v) {
        v.visitAttribute(this);
    }
}