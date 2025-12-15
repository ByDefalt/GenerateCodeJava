package metaModel;

public class Type implements MinispecElement {
    private String baseType; // String, Integer, ou nom d'une entité
    private boolean isCollection;
    private String collectionType; // List, Set, Bag, Array
    private Integer minCardinality;
    private Integer maxCardinality;
    private Integer arraySize; // Pour les Array uniquement

    // Constructeur pour type simple
    public Type(String baseType) {
        this.baseType = baseType;
        this.isCollection = false;
    }

    // Constructeur pour collection
    public Type(String baseType, String collectionType) {
        this.baseType = baseType;
        this.isCollection = true;
        this.collectionType = collectionType;
    }

    // Constructeur complet pour collection avec cardinalités
    public Type(String baseType, String collectionType, Integer minCardinality, Integer maxCardinality) {
        this.baseType = baseType;
        this.isCollection = true;
        this.collectionType = collectionType;
        this.minCardinality = minCardinality;
        this.maxCardinality = maxCardinality;
    }

    // Constructeur pour Array avec taille fixe
    public Type(String baseType, Integer arraySize) {
        this.baseType = baseType;
        this.isCollection = true;
        this.collectionType = "Array";
        this.arraySize = arraySize;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean isCollection) {
        this.isCollection = isCollection;
    }

    public String getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
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

    public Integer getArraySize() {
        return arraySize;
    }

    public void setArraySize(Integer arraySize) {
        this.arraySize = arraySize;
    }

    @Override
    public void accept(Visitor v) {
        v.visitType(this);
    }
}