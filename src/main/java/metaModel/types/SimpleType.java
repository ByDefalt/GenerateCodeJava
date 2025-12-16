package metaModel.types;

import visitor.Visitor;

/**
 * Représente un type simple (primitif ou référence à une entité)
 * Exemples: String, Integer, Satellite, Point
 */
public class SimpleType extends Type {
    private String typeName;

    public SimpleType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String getMinispecTypeName() {
        return typeName;
    }

    @Override
    public String getJavaTypeName() {
        return typeName;
    }

    @Override
    public String getBaseType() {
        return typeName;
    }

    @Override
    public boolean isPrimitive() {
        // Liste des types primitifs Java de base
        return typeName.equals("String") ||
                typeName.equals("Integer") ||
                typeName.equals("Double") ||
                typeName.equals("Boolean") ||
                typeName.equals("Long") ||
                typeName.equals("Float") ||
                typeName.equals("int") ||
                typeName.equals("double") ||
                typeName.equals("boolean") ||
                typeName.equals("long") ||
                typeName.equals("float");
    }

    @Override
    public boolean isEntityReference() {
        return !isPrimitive();
    }

    @Override
    public void accept(Visitor v) {

    }
}