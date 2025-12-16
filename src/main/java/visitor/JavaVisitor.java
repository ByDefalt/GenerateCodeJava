package visitor;

import metaModel.*;
import metaModel.types.*;
import metaModel.types.Type;

import java.util.HashSet;
import java.util.Set;

/**
 * Visiteur pour générer du code Java
 * Toute la logique spécifique à Java est dans ce visiteur
 * Le métamodèle reste indépendant du langage
 */
public class JavaVisitor extends Visitor {
    private String resultBuffer = "";
    private String methodBuffer = "";
    private Set<String> imports = new HashSet<>();
    private String currentJavaType = "";

    @Override
    public void visitModel(Model e) {
        // Générer toutes les classes
        if (e.getEntities() != null) {
            for (Entity ent : e.getEntities()) {
                ent.accept(this);
            }
        }
    }

    @Override
    public void visitEntity(Entity e) {
        // Réinitialiser pour chaque entité
        methodBuffer = "";
        imports.clear();

        StringBuilder classBuffer = new StringBuilder();
        StringBuilder attributesBuffer = new StringBuilder();

        // Ajouter les imports nécessaires
        collectImports(e);

        if (!imports.isEmpty()) {
            for (String imp : imports) {
                classBuffer.append("import ").append(imp).append(";\n");
            }
            classBuffer.append("\n");
        }

        // Déclaration de la classe avec héritage si présent
        classBuffer.append("public class ").append(e.getName());

        classBuffer.append(" {\n");

        // Visiter les attributs
        if (e.getAttributes() != null) {
            for (Attribute a : e.getAttributes()) {
                String savedResultBuffer = resultBuffer;
                resultBuffer = "";

                a.accept(this);

                attributesBuffer.append(resultBuffer);
                resultBuffer = savedResultBuffer;
            }
        }

        // Ajouter les attributs puis les méthodes
        classBuffer.append(attributesBuffer);
        classBuffer.append(methodBuffer);
        classBuffer.append("}\n\n");

        resultBuffer += classBuffer.toString();
    }

    private void collectImports(Entity e) {
        if (e.getAttributes() != null) {
            for (Attribute a : e.getAttributes()) {
                Type type = a.getType();

                if (type instanceof ListType) {
                    imports.add("java.util.List");
                    imports.add("java.util.ArrayList");
                } else if (type instanceof SetType) {
                    imports.add("java.util.Set");
                    imports.add("java.util.HashSet");
                } else if (type instanceof BagType) {
                    imports.add("java.util.List");
                    imports.add("java.util.ArrayList");
                }
            }
        }
    }

    @Override
    public void visitAttribute(Attribute e) {
        currentJavaType = "";
        e.getType().accept(this);

        String javaType = currentJavaType;
        Type type = e.getType();

        resultBuffer += "    private " + javaType + " " + e.getName();
        resultBuffer += ";\n";

        String capitalizedName = e.getName().substring(0, 1).toUpperCase() + e.getName().substring(1);

        // Getter
        methodBuffer += "\n    public " + javaType + " get" + capitalizedName + "() {\n";
        methodBuffer += "        return " + e.getName() + ";\n";
        methodBuffer += "    }\n";

        // Setter
        methodBuffer += "\n    public void set" + capitalizedName + "(" + javaType + " " + e.getName() + ") {\n";
        methodBuffer += "        this." + e.getName() + " = " + e.getName() + ";\n";
        methodBuffer += "    }\n";

        // Pour les collections, ajouter des méthodes utilitaires
        if (type.isCollection()) {
            generateCollectionMethods(e, type, capitalizedName);
        }
    }

    @Override
    public void visitSimpleType(SimpleType e) {
        currentJavaType = e.getTypeName();
    }

    @Override
    public void visitArrayType(ArrayType e) {
        currentJavaType = e.getElementType() + "[]";
    }

    @Override
    public void visitListType(ListType e) {
        currentJavaType = "List<" + e.getElementType() + ">";
    }

    @Override
    public void visitSetType(SetType e) {
        currentJavaType = "Set<" + e.getElementType() + ">";
    }

    @Override
    public void visitBagType(BagType e) {
        // Bag implémenté comme List en Java
        currentJavaType = "List<" + e.getElementType() + ">";
    }

    private void generateCollectionMethods(Attribute attr, Type type, String capitalizedName) {
        String baseType = type.getBaseType();
        String attrName = attr.getName();

        if (type instanceof ArrayType) {
            // Pour les arrays, générer des méthodes d'accès par index
            methodBuffer += "\n    public " + baseType + " get" + capitalizedName + "At(int index) {\n";
            methodBuffer += "        return " + attrName + "[index];\n";
            methodBuffer += "    }\n";

            methodBuffer += "\n    public void set" + capitalizedName + "At(int index, " + baseType + " value) {\n";
            methodBuffer += "        " + attrName + "[index] = value;\n";
            methodBuffer += "    }\n";

        } else if (type instanceof CollectionType) {
            CollectionType collType = (CollectionType) type;

            String singularName = capitalizedName.endsWith("s") ?
                    capitalizedName.substring(0, capitalizedName.length() - 1) :
                    capitalizedName;

            // Déterminer le type d'implémentation
            String implType;
            if (type instanceof SetType) {
                implType = "HashSet";
            } else {
                implType = "ArrayList";
            }

            // Méthode add
            methodBuffer += "\n    public void add" + singularName + "(" + baseType + " item) {\n";
            methodBuffer += "        if (this." + attrName + " == null) {\n";
            methodBuffer += "            this." + attrName + " = new " + implType + "<>();\n";
            methodBuffer += "        }\n";

            // Vérifier les cardinalités si elles existent
            if (collType.getMaxCardinality() != null) {
                methodBuffer += "        if (this." + attrName + ".size() >= " + collType.getMaxCardinality() + ") {\n";
                methodBuffer += "            throw new IllegalStateException(\"Cannot add more than " + collType.getMaxCardinality() + " items\");\n";
                methodBuffer += "        }\n";
            }

            methodBuffer += "        this." + attrName + ".add(item);\n";
            methodBuffer += "    }\n";

            // Méthode remove
            methodBuffer += "\n    public void remove" + singularName + "(" + baseType + " item) {\n";
            methodBuffer += "        if (this." + attrName + " != null) {\n";

            // Vérifier les cardinalités minimales si elles existent
            if (collType.getMinCardinality() != null) {
                methodBuffer += "            if (this." + attrName + ".size() <= " + collType.getMinCardinality() + ") {\n";
                methodBuffer += "                throw new IllegalStateException(\"Cannot have less than " + collType.getMinCardinality() + " items\");\n";
                methodBuffer += "            }\n";
            }

            methodBuffer += "            this." + attrName + ".remove(item);\n";
            methodBuffer += "        }\n";
            methodBuffer += "    }\n";

            // Méthode size
            methodBuffer += "\n    public int get" + capitalizedName + "Size() {\n";
            methodBuffer += "        return this." + attrName + " != null ? this." + attrName + ".size() : 0;\n";
            methodBuffer += "    }\n";
        }
    }

    public String getResult() {
        return resultBuffer;
    }
}