package metaModel.types;

import visitor.Visitor;

/**
 * Représente un type List
 * Exemple: List of Satellite, List[1:10] of String
 */
public class ListType extends CollectionType {

    public ListType(String elementType) {
        super(elementType);
    }

    public ListType(String elementType, Integer minCardinality, Integer maxCardinality) {
        super(elementType, minCardinality, maxCardinality);
    }

    @Override
    public String getCollectionTypeName() {
        return "List";
    }

    @Override
    public String getMinispecTypeName() {
        StringBuilder sb = new StringBuilder("List");

        if (hasCardinality()) {
            sb.append(" [");
            sb.append(minCardinality != null ? minCardinality : "*");
            sb.append(":");
            sb.append(maxCardinality != null ? maxCardinality : "*");
            sb.append("]");
        }

        sb.append(" of ").append(elementType);
        return sb.toString();
    }

    @Override
    public String getJavaTypeName() {
        return "List<" + elementType + ">";
    }

    @Override
    public String getJavaImport() {
        return "java.util.List";
    }

    @Override
    public String getJavaImplementationType() {
        return "ArrayList";
    }

    @Override
    public void accept(Visitor v) {

    }
}