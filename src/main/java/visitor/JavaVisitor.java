package visitor;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.types.Type;

public class JavaVisitor extends Visitor {
    private String resultBuffer = "";
    private String methodBuffer = "";
    private String currentJavaType = "";

    @Override
    public void visitModel(Model e) {
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


        classBuffer.append("public class ").append(e.getName()).append(" {\n");

        if (e.getAttributes() != null) {
            for (Attribute a : e.getAttributes()) {
                String savedResultBuffer = resultBuffer;
                resultBuffer = "";

                a.accept(this);

                attributesBuffer.append(resultBuffer);
                resultBuffer = savedResultBuffer;
            }
        }

        classBuffer.append(attributesBuffer);
        classBuffer.append(methodBuffer);
        classBuffer.append("}\n\n");

        resultBuffer += classBuffer.toString();
    }

    @Override
    public void visitAttribute(Attribute e) {
        currentJavaType = "";
        e.getType().accept(this);

        String javaType = currentJavaType;
        Type type = e.getType();

        resultBuffer += "    private " + javaType + " " + e.getName() + ";\n";

        String capitalizedName = e.getName().substring(0, 1).toUpperCase() + e.getName().substring(1);

        methodBuffer += "\n    public " + javaType + " get" + capitalizedName + "() {\n";
        methodBuffer += "        return " + e.getName() + ";\n";
        methodBuffer += "    }\n";

        methodBuffer += "\n    public void set" + capitalizedName + "(" + javaType + " " + e.getName() + ") {\n";
        methodBuffer += "        this." + e.getName() + " = " + e.getName() + ";\n";
        methodBuffer += "    }\n";

        if (type.isCollection()) {
            generateCollectionMethods(e, type, capitalizedName);
        }
    }

    @Override
    public void visitType(Type e) {
        if (!e.isCollection()) {
            currentJavaType = e.getBaseType();
        } else {
            if ("Array".equals(e.getCollectionType())) {
                currentJavaType = e.getBaseType() + "[]";
            } else if ("List".equals(e.getCollectionType())) {
                currentJavaType = "List<" + e.getBaseType() + ">";
            } else if ("Set".equals(e.getCollectionType())) {
                currentJavaType = "Set<" + e.getBaseType() + ">";
            } else if ("Bag".equals(e.getCollectionType())) {
                currentJavaType = "List<" + e.getBaseType() + ">";
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
            methodBuffer += "\n    public " + baseType + " get" + capitalizedName + "At(int index) {\n";
            methodBuffer += "        return " + attrName + "[index];\n";
            methodBuffer += "    }\n";

            methodBuffer += "\n    public void set" + capitalizedName + "At(int index, " + baseType + " value) {\n";
            methodBuffer += "        " + attrName + "[index] = value;\n";
            methodBuffer += "    }\n";

        } else {
            String singularName = capitalizedName.endsWith("s")
                    ? capitalizedName.substring(0, capitalizedName.length() - 1)
                    : capitalizedName;

            methodBuffer += "\n    public void add" + singularName + "(" + baseType + " item) {\n";
            methodBuffer += "        if (this." + attrName + " == null) {\n";
            if ("List".equals(collectionType) || "Bag".equals(collectionType)) {
                methodBuffer += "            this." + attrName + " = new ArrayList<>();\n";
            } else if ("Set".equals(collectionType)) {
                methodBuffer += "            this." + attrName + " = new HashSet<>();\n";
            }
            methodBuffer += "        }\n";

            if (type.getMaxCardinality() != null) {
                methodBuffer += "        if (this." + attrName + ".size() >= " + type.getMaxCardinality() + ") {\n";
                methodBuffer += "            throw new IllegalStateException(\"Cannot add more than " + type.getMaxCardinality() + " items\");\n";
                methodBuffer += "        }\n";
            }

            methodBuffer += "        this." + attrName + ".add(item);\n";
            methodBuffer += "    }\n";

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

            methodBuffer += "\n    public int get" + capitalizedName + "Size() {\n";
            methodBuffer += "        return this." + attrName + " != null ? this." + attrName + ".size() : 0;\n";
            methodBuffer += "    }\n";
        }
    }

    public String getResult() {
        return resultBuffer;
    }
}
