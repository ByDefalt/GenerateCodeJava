package metaModel;

public class Attribute implements MinispecElement {
    private String name;
    private Type type;

    // Constructeur avec String (pour compatibilité)
    public Attribute(String name, String typeStr) {
        this.name = name;
        this.type = new Type(typeStr);
    }

    // Constructeur avec Type
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

    // Pour compatibilité avec l'ancien code
    public void setType(String typeStr) {
        this.type = new Type(typeStr);
    }

    @Override
    public void accept(Visitor v) {
        v.visitAttribute(this);
    }
}