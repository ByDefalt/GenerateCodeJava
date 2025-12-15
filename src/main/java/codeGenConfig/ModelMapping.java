package codeGenConfig;

/**
 * Représente le mapping d'un modèle minispec vers un package Java
 */
public class ModelMapping {
    private String modelName;  // Nom du modèle minispec
    private String javaPackage; // Package Java correspondant

    public ModelMapping(String modelName, String javaPackage) {
        this.modelName = modelName;
        this.javaPackage = javaPackage;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getJavaPackage() {
        return javaPackage;
    }

    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }
}
