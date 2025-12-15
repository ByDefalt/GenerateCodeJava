package visitor;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.Type;
import metaModel.Visitor;
import java.util.HashSet;
import java.util.Set;

public class JavaVisitor extends Visitor {
    private String resultBuffer = "";
    private String methodBuffer = "";
    private Set<String> imports = new HashSet<>();
    private String currentJavaType = ""; // Pour stocker temporairement le type Java

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

        classBuffer.append("public class ").append(e.getName()).append(" {\n");

        // Visiter les attributs (ils seront ajoutés à attributesBuffer temporairement)
        if (e.getAttributes() != null) {
            for (Attribute a : e.getAttributes()) {
                // Sauvegarder le resultBuffer actuel
                String savedResultBuffer = resultBuffer;
                resultBuffer = "";

                a.accept(this);

                // Récupérer l'attribut généré et le mettre dans attributesBuffer
                attributesBuffer.append(resultBuffer);

                // Restaurer le resultBuffer
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
                if (type.isCollection()) {
                    String collectionType = type.getCollectionType();
                    if ("List".equals(collectionType)) {
                        imports.add("java.util.List");
                        imports.add("java.util.ArrayList");
                    } else if ("Set".equals(collectionType)) {
                        imports.add("java.util.Set");
                        imports.add("java.util.HashSet");
                    } else if ("Bag".equals(collectionType)) {
                        imports.add("java.util.List");
                        imports.add("java.util.ArrayList");
                    }
                }
            }
        }
    }

    @Override
    public void visitAttribute(Attribute e) {
        // Visiter le type pour obtenir sa représentation Java
        currentJavaType = "";
        e.getType().accept(this);

        String javaType = currentJavaType;
        Type type = e.getType();

        // Ajouter l'attribut (variable d'instance)
        resultBuffer += "    private " + javaType + " " + e.getName() + ";\n";

        // Générer les getters et setters
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
    public void visitType(Type e) {
        if (!e.isCollection()) {
            // Type simple
            currentJavaType = e.getBaseType();
        } else {
            // Collection
            if ("Array".equals(e.getCollectionType())) {
                currentJavaType = e.getBaseType() + "[]";
            } else if ("List".equals(e.getCollectionType())) {
                currentJavaType = "List<" + e.getBaseType() + ">";
            } else if ("Set".equals(e.getCollectionType())) {
                currentJavaType = "Set<" + e.getBaseType() + ">";
            } else if ("Bag".equals(e.getCollectionType())) {
                currentJavaType = "List<" + e.getBaseType() + ">"; // Bag implémenté comme List
            } else {
                currentJavaType = e.getBaseType();
            }
        }
    }

    private void generateCollectionMethods(Attribute attr, Type type, String capitalizedName) {
        String collectionType = type.getCollectionType();
        String baseType = type.getBaseType();
        String attrName = attr.getName();

        if ("Array".equals(collectionType)) {
            // Pour les arrays, générer des méthodes d'accès par index
            methodBuffer += "\n    public " + baseType + " get" + capitalizedName + "At(int index) {\n";
            methodBuffer += "        return " + attrName + "[index];\n";
            methodBuffer += "    }\n";

            methodBuffer += "\n    public void set" + capitalizedName + "At(int index, " + baseType + " value) {\n";
            methodBuffer += "        " + attrName + "[index] = value;\n";
            methodBuffer += "    }\n";

        } else {
            // Pour List, Set, Bag - ajouter add/remove
            String singularName = capitalizedName.endsWith("s") ?
                    capitalizedName.substring(0, capitalizedName.length() - 1) :
                    capitalizedName;

            // Méthode add
            methodBuffer += "\n    public void add" + singularName + "(" + baseType + " item) {\n";
            methodBuffer += "        if (this." + attrName + " == null) {\n";
            if ("List".equals(collectionType) || "Bag".equals(collectionType)) {
                methodBuffer += "            this." + attrName + " = new ArrayList<>();\n";
            } else if ("Set".equals(collectionType)) {
                methodBuffer += "            this." + attrName + " = new HashSet<>();\n";
            }
            methodBuffer += "        }\n";

            // Vérifier les cardinalités si elles existent
            if (type.getMaxCardinality() != null) {
                methodBuffer += "        if (this." + attrName + ".size() >= " + type.getMaxCardinality() + ") {\n";
                methodBuffer += "            throw new IllegalStateException(\"Cannot add more than " + type.getMaxCardinality() + " items\");\n";
                methodBuffer += "        }\n";
            }

            methodBuffer += "        this." + attrName + ".add(item);\n";
            methodBuffer += "    }\n";

            // Méthode remove
            methodBuffer += "\n    public void remove" + singularName + "(" + baseType + " item) {\n";
            methodBuffer += "        if (this." + attrName + " != null) {\n";

            // Vérifier les cardinalités minimales si elles existent
            if (type.getMinCardinality() != null) {
                methodBuffer += "            if (this." + attrName + ".size() <= " + type.getMinCardinality() + ") {\n";
                methodBuffer += "                throw new IllegalStateException(\"Cannot have less than " + type.getMinCardinality() + " items\");\n";
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