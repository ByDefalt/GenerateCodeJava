package codeGenConfig;

/**
 * Représente le mapping d'un type primitif minispec vers un type Java
 */
public class PrimitiveMapping {
    private String primitiveName; // Nom du type primitif minispec (String, Integer, List, etc.)
    private String javaType;      // Type Java correspondant (String, Integer, ArrayList, etc.)
    private String javaPackage;   // Package Java à importer (peut être null pour les types de base)

    public PrimitiveMapping(String primitiveName, String javaType) {
        this.primitiveName = primitiveName;
        this.javaType = javaType;
        this.javaPackage = null;
    }

    public PrimitiveMapping(String primitiveName, String javaType, String javaPackage) {
        this.primitiveName = primitiveName;
        this.javaType = javaType;
        this.javaPackage = javaPackage;
    }

    public String getPrimitiveName() {
        return primitiveName;
    }

    public void setPrimitiveName(String primitiveName) {
        this.primitiveName = primitiveName;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getJavaPackage() {
        return javaPackage;
    }

    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }

    public boolean hasPackage() {
        return javaPackage != null && !javaPackage.isEmpty();
    }

    /**
     * Retourne l'import complet (package + type)
     */
    public String getFullImport() {
        if (hasPackage()) {
            return javaPackage;
        }
        return null;
    }
}
