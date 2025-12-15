package metaModel;

public class Attribute implements MinispecElement {
    String name;
    String type;

    public Attribute(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    @Override
    public void accept(Visitor v) {
        v.visitAttribute(this);
    }
}
