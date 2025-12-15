package metaModel;

public class Attribute implements MinispecElement {
    private String name;
    private Type type;
    private String initialValue; // Expression d'initialisation (peut être null)

    // Constructeur avec String (pour compatibilité)
    public Attribute(String name, String typeStr) {
        this.name = name;
        this.type = new Type(typeStr);
        this.initialValue = null;
    }

    // Constructeur avec Type
    public Attribute(String name, Type type) {
        this.name = name;
        this.type = type;
        this.initialValue = null;
    }

    // Constructeur complet avec valeur initiale
    public Attribute(String name, Type type, String initialValue) {
        this.name = name;
        this.type = type;
        this.initialValue = initialValue;
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

    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    public boolean hasInitialValue() {
        return initialValue != null && !initialValue.isEmpty();
    }

    @Override
    public void accept(Visitor v) {
        v.visitAttribute(this);
    }
}