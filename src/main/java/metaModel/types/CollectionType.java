package metaModel.types;

/**
 * Classe abstraite pour tous les types collection
 * Suit le principe OCP pour permettre l'ajout de nouveaux types de collections
 */
public abstract class CollectionType extends Type {
    protected String elementType;
    protected Integer minCardinality;
    protected Integer maxCardinality;

    public CollectionType(String elementType) {
        this.elementType = elementType;
        this.minCardinality = null;
        this.maxCardinality = null;
    }

    public CollectionType(String elementType, Integer minCardinality, Integer maxCardinality) {
        this.elementType = elementType;
        this.minCardinality = minCardinality;
        this.maxCardinality = maxCardinality;
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public String getBaseType() {
        return elementType;
    }

    public String getElementType() {
        return elementType;
    }

    public Integer getMinCardinality() {
        return minCardinality;
    }

    public void setMinCardinality(Integer minCardinality) {
        this.minCardinality = minCardinality;
    }

    public Integer getMaxCardinality() {
        return maxCardinality;
    }

    public void setMaxCardinality(Integer maxCardinality) {
        this.maxCardinality = maxCardinality;
    }

    public boolean hasCardinality() {
        return minCardinality != null || maxCardinality != null;
    }

    /**
     * Retourne le type de collection (List, Set, etc.)
     */
    public abstract String getCollectionTypeName();

    /**
     * Retourne l'import Java nécessaire pour ce type de collection
     */
    public abstract String getJavaImport();

    /**
     * Retourne le type d'implémentation Java (ArrayList, HashSet, etc.)
     */
    public abstract String getJavaImplementationType();
}