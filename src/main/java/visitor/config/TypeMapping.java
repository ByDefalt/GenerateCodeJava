package visitor.config;

import java.util.HashSet;
import java.util.Set;

/**
 * Représente le mapping d'un type primitif vers un type Java.
 * Inclut le type Java cible et les imports nécessaires.
 */
public class TypeMapping {
    private final String primitiveType;
    private final String javaType;
    private final Set<String> requiredImports;
    
    public TypeMapping(String primitiveType, String javaType) {
        this.primitiveType = primitiveType;
        this.javaType = javaType;
        this.requiredImports = new HashSet<>();
    }
    
    public String getPrimitiveType() {
        return primitiveType;
    }
    
    public String getJavaType() {
        return javaType;
    }
    
    public Set<String> getRequiredImports() {
        return requiredImports;
    }
    
    public void addImport(String importStr) {
        requiredImports.add(importStr);
    }
    
    @Override
    public String toString() {
        return primitiveType + " -> " + javaType + 
               (requiredImports.isEmpty() ? "" : " (imports: " + requiredImports + ")");
    }
}
