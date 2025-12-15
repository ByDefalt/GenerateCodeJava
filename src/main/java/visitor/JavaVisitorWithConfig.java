package visitor;

import codeGenConfig.CodeGenConfig;
import codeGenConfig.DependencyResolver;
import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.Type;
import metaModel.Visitor;
import java.util.HashSet;
import java.util.Set;

public class JavaVisitorWithConfig extends Visitor {
    private String resultBuffer = "";
    private String methodBuffer = "";
    private Set<String> imports = new HashSet<>();
    private String currentJavaType = "";
    private CodeGenConfig config;
    private DependencyResolver dependencyResolver;
    private Model currentModel;

    public JavaVisitorWithConfig(CodeGenConfig config) {
        this.config = config;
        this.dependencyResolver = new DependencyResolver(config);
    }

    @Override
    public void visitModel(Model e) {
        this.currentModel = e;

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

        // Collecter les imports nécessaires
        collectImports(e);

        // Ajouter le package si défini dans la config
        String packageName = config.getPackageForModel(currentModel.getName());
        if (packageName != null && !packageName.isEmpty()) {
            classBuffer.append("package ").append(packageName).append(";\n\n");
        }

        // Ajouter les imports
        if (!imports.isEmpty()) {
            for (String imp : imports) {
                classBuffer.append("import ").append(imp).append(";\n");
            }
            classBuffer.append("\n");
        }

        // Déclaration de la classe avec héritage si présent
        classBuffer.append("public class ").append(e.getName());
        if (e.hasSuperType()) {
            classBuffer.append(" extends ").append(e.getSuperType());
        }
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
        // Utiliser le DependencyResolver pour calculer les imports
        Set<String> computedImports = dependencyResolver.computeImportsForEntity(e, currentModel);
        imports.addAll(computedImports);

        // Ajouter les imports pour les collections utilisées
        if (e.getAttributes() != null) {
            for (Attribute a : e.getAttributes()) {
                Type type = a.getType();
                if (type.isCollection()) {
                    String collectionType = type.getCollectionType();

                    // Utiliser la config pour obtenir les imports appropriés
                    String collectionImport = config.getImportForPrimitive(collectionType);
                    if (collectionImport != null) {
                        imports.add(collectionImport);
                    }

                    // Ajouter aussi l'interface si nécessaire
                    if ("List".equals(collectionType)) {
                        imports.add("java.util.List");
                    } else if ("Set".equals(collectionType)) {
                        imports.add("java.util.Set");
                    } else if ("Map".equals(collectionType)) {
                        imports.add("java.util.Map");
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
            // Type simple - utiliser la config pour obtenir le type Java
            String javaType = config.getJavaTypeForPrimitive(e.getBaseType());
            currentJavaType = javaType;
        } else {
            // Collection - utiliser la config pour obtenir le type approprié
            String collectionJavaType = config.getJavaTypeForPrimitive(e.getCollectionType());
            String baseJavaType = config.getJavaTypeForPrimitive(e.getBaseType());

            if ("Array".equals(e.getCollectionType())) {
                currentJavaType = baseJavaType + "[]";
            } else {
                // Pour List, Set, utiliser l'interface
                String interfaceType = e.getCollectionType();
                currentJavaType = interfaceType + "<" + baseJavaType + ">";
            }
        }
    }

    private void generateCollectionMethods(Attribute attr, Type type, String capitalizedName) {
        String collectionType = type.getCollectionType();
        String baseType = config.getJavaTypeForPrimitive(type.getBaseType());
        String attrName = attr.getName();

        if ("Array".equals(collectionType)) {
            // Pour les arrays
            methodBuffer += "\n    public " + baseType + " get" + capitalizedName + "At(int index) {\n";
            methodBuffer += "        return " + attrName + "[index];\n";
            methodBuffer += "    }\n";

            methodBuffer += "\n    public void set" + capitalizedName + "At(int index, " + baseType + " value) {\n";
            methodBuffer += "        " + attrName + "[index] = value;\n";
            methodBuffer += "    }\n";

        } else {
            // Pour List, Set, Bag
            String singularName = capitalizedName.endsWith("s") ?
                    capitalizedName.substring(0, capitalizedName.length() - 1) :
                    capitalizedName;

            String implementationType = config.getJavaTypeForPrimitive(collectionType);

            // Méthode add
            methodBuffer += "\n    public void add" + singularName + "(" + baseType + " item) {\n";
            methodBuffer += "        if (this." + attrName + " == null) {\n";
            methodBuffer += "            this." + attrName + " = new " + implementationType + "<>();\n";
            methodBuffer += "        }\n";

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