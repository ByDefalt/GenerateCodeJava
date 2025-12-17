package metaModel.minispec;

import metaModel.minispec.types.Type;
import visitor.Visitor;

public class Attribute implements MinispecElement {
    private final String name;
    private final Type type;
    private final String initialValue;


    public Attribute(String name, Type type, String initialValue) {
        this.name = name;
        this.type = type;
        this.initialValue = initialValue;
    }

    public Attribute(String name, Type type) {

        this.name = name;
        this.type = type;
        this.initialValue = null;
    }

    public String getInitialValue() {
        return initialValue;
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