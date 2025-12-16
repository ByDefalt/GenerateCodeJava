package visitor;

import metaModel.*;
import metaModel.types.*;

import java.util.HashSet;
import java.util.Set;

public class JavaVisitor extends Visitor {
    private String resultBuffer = "";
    private String methodBuffer = "";

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
        methodBuffer = "";

        StringBuilder classBuffer = new StringBuilder();
        StringBuilder attributesBuffer = new StringBuilder();



        classBuffer.append("public class ").append(e.getName());
        if (e.getSuperEntity() != null) {
            classBuffer.append(" extends ").append(e.getSuperEntity().getName());
        }
        classBuffer.append(" {\n");

        if (e.getAttributes() != null) {
            for (Attribute a : e.getAttributes()) {
                String savedResult = resultBuffer;
                resultBuffer = "";

                a.accept(this);

                attributesBuffer.append(resultBuffer);
                resultBuffer = savedResult;
            }
        }

        classBuffer.append(attributesBuffer);
        classBuffer.append(methodBuffer);
        classBuffer.append("}\n\n");

        String existingContent = this.resultBuffer;
        this.resultBuffer = existingContent + classBuffer.toString();
    }


    @Override
    public void visitAttribute(Attribute e) {
        currentJavaType = "";
        e.getType().accept(this);
        String javaFullType = currentJavaType;

        // Génération du champ
        resultBuffer += "    private " + javaFullType + " " + e.getName() + ";\n";

        String capitalizedName = e.getName().substring(0, 1).toUpperCase() + e.getName().substring(1);

        // Génération du Getter
        methodBuffer += "\n    public " + javaFullType + " get" + capitalizedName + "() {\n";
        methodBuffer += "        return " + e.getName() + ";\n";
        methodBuffer += "    }\n";

        // Génération du Setter
        methodBuffer += "\n    public void set" + capitalizedName + "(" + javaFullType + " " + e.getName() + ") {\n";
        methodBuffer += "        this." + e.getName() + " = " + e.getName() + ";\n";
        methodBuffer += "    }\n";

        // Génération des méthodes utilitaires pour les collections
        // On vérifie le type instance car isCollection() n'existe pas dans Type
        if (e.getType() instanceof CollectionType) {
            generateCollectionMethods(e, (CollectionType) e.getType(), capitalizedName);
        }
    }

    // --- Visite des Types ---

    @Override
    public void visitSimpleType(SimpleType e) {
        currentJavaType = e.getTypeName();
    }

    @Override
    public void visitResolvedReference(ResolvedReference e) {
        // On utilise le nom de l'entité référencée
        currentJavaType = e.getReferencedEntity().getName();
    }

    @Override
    public void visitUnresolvedReference(UnresolvedReference e) {
        // On utilise le nom stocké (chaîne de caractères)
        currentJavaType = e.getEntityName();
    }

    @Override
    public void visitArrayType(ArrayType e) {
        // On doit visiter le sous-type pour obtenir son nom correct
        e.getElementType().accept(this);
        String elementTypeStr = currentJavaType;
        currentJavaType = elementTypeStr + "[]";
    }

    @Override
    public void visitListType(ListType e) {
        e.getElementType().accept(this);
        String elementTypeStr = currentJavaType;
        currentJavaType = "List<" + elementTypeStr + ">";
    }

    @Override
    public void visitSetType(SetType e) {
        e.getElementType().accept(this);
        String elementTypeStr = currentJavaType;
        currentJavaType = "Set<" + elementTypeStr + ">";
    }

    @Override
    public void visitBagType(BagType e) {
        // Bag est souvent implémenté comme une List
        e.getElementType().accept(this);
        String elementTypeStr = currentJavaType;
        currentJavaType = "List<" + elementTypeStr + ">";
    }

    // --- Helpers ---

    private void generateCollectionMethods(Attribute attr, CollectionType type, String capitalizedName) {
        // Pour générer add/remove, il nous faut le type de l'élément (le "base type")
        // On visite le sous-type pour récupérer sa chaîne Java (ex: "String", "Person")
        type.getElementType().accept(this);
        String baseType = currentJavaType;

        String attrName = attr.getName();

        if (type instanceof ArrayType) {
            // Pour les arrays
            methodBuffer += "\n    public " + baseType + " get" + capitalizedName + "At(int index) {\n";
            methodBuffer += "        return " + attrName + "[index];\n";
            methodBuffer += "    }\n";

            methodBuffer += "\n    public void set" + capitalizedName + "At(int index, " + baseType + " value) {\n";
            methodBuffer += "        " + attrName + "[index] = value;\n";
            methodBuffer += "    }\n";

        } else {
            // List, Set, Bag
            String singularName = capitalizedName.endsWith("s") ?
                    capitalizedName.substring(0, capitalizedName.length() - 1) :
                    capitalizedName;

            String implType;
            if (type instanceof SetType) {
                implType = "HashSet";
            } else {
                implType = "ArrayList"; // Pour List et Bag
            }

            // Méthode ADD
            methodBuffer += "\n    public void add" + singularName + "(" + baseType + " item) {\n";
            methodBuffer += "        if (this." + attrName + " == null) {\n";
            methodBuffer += "            this." + attrName + " = new " + implType + "<>();\n";
            methodBuffer += "        }\n";

            // Vérification Max Cardinality
            if (type.getMaxCardinality() != null) {
                methodBuffer += "        if (this." + attrName + ".size() >= " + type.getMaxCardinality() + ") {\n";
                methodBuffer += "            throw new IllegalStateException(\"Cannot add more than " + type.getMaxCardinality() + " items\");\n";
                methodBuffer += "        }\n";
            }

            methodBuffer += "        this." + attrName + ".add(item);\n";
            methodBuffer += "    }\n";

            // Méthode REMOVE
            methodBuffer += "\n    public void remove" + singularName + "(" + baseType + " item) {\n";
            methodBuffer += "        if (this." + attrName + " != null) {\n";

            // Vérification Min Cardinality
            if (type.getMinCardinality() != null) {
                methodBuffer += "            if (this." + attrName + ".size() <= " + type.getMinCardinality() + ") {\n";
                methodBuffer += "                throw new IllegalStateException(\"Cannot have less than " + type.getMinCardinality() + " items\");\n";
                methodBuffer += "            }\n";
            }

            methodBuffer += "            this." + attrName + ".remove(item);\n";
            methodBuffer += "        }\n";
            methodBuffer += "    }\n";

            // Méthode SIZE
            methodBuffer += "\n    public int get" + capitalizedName + "Size() {\n";
            methodBuffer += "        return this." + attrName + " != null ? this." + attrName + ".size() : 0;\n";
            methodBuffer += "    }\n";
        }
    }

    public String getResult() {
        return resultBuffer;
    }
}