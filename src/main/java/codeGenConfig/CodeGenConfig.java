package codeGenConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration pour la génération de code Java
 * Contient les mappings entre modèles minispec et packages Java,
 * ainsi que les mappings des types primitifs
 */
public class CodeGenConfig {
    private Map<String, ModelMapping> modelMappings;      // modelName -> ModelMapping
    private Map<String, PrimitiveMapping> primitiveMappings; // primitiveName -> PrimitiveMapping

    public CodeGenConfig() {
        this.modelMappings = new HashMap<>();
        this.primitiveMappings = new HashMap<>();
    }

    public void addModelMapping(ModelMapping mapping) {
        this.modelMappings.put(mapping.getModelName(), mapping);
    }

    public void addPrimitiveMapping(PrimitiveMapping mapping) {
        this.primitiveMappings.put(mapping.getPrimitiveName(), mapping);
    }

    public ModelMapping getModelMapping(String modelName) {
        return this.modelMappings.get(modelName);
    }

    public PrimitiveMapping getPrimitiveMapping(String primitiveName) {
        return this.primitiveMappings.get(primitiveName);
    }

    public String getPackageForModel(String modelName) {
        ModelMapping mapping = getModelMapping(modelName);
        return mapping != null ? mapping.getJavaPackage() : null;
    }

    public String getJavaTypeForPrimitive(String primitiveName) {
        PrimitiveMapping mapping = getPrimitiveMapping(primitiveName);
        return mapping != null ? mapping.getJavaType() : primitiveName;
    }

    public String getImportForPrimitive(String primitiveName) {
        PrimitiveMapping mapping = getPrimitiveMapping(primitiveName);
        return mapping != null ? mapping.getFullImport() : null;
    }

    public Map<String, ModelMapping> getModelMappings() {
        return modelMappings;
    }

    public Map<String, PrimitiveMapping> getPrimitiveMappings() {
        return primitiveMappings;
    }
}