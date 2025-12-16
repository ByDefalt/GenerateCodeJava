package metaModel.types;

import metaModel.MinispecElement;
import visitor.Visitor;

/**
 * Classe abstraite de base pour tous les types
 * Suit le principe Open/Closed Principle (OCP)
 */
public abstract class Type implements MinispecElement {

    /**
     * Retourne le nom du type tel qu'utilisé dans minispec
     */
    public abstract String getMinispecTypeName();

    /**
     * Retourne le nom du type Java correspondant
     */
    public abstract String getJavaTypeName();

    /**
     * Indique si ce type est une collection
     */
    public boolean isCollection() {
        return false;
    }

    /**
     * Indique si ce type est un type primitif
     */
    public boolean isPrimitive() {
        return false;
    }

    /**
     * Indique si ce type est une référence à une entité
     */
    public boolean isEntityReference() {
        return false;
    }

    /**
     * Retourne le type de base (pour les collections, c'est le type des éléments)
     */
    public abstract String getBaseType();

    @Override
    public void accept(Visitor v) {
        v.visitType(this);
    }
}