package visitor.imports;

import metaModel.Attribute;
import metaModel.MinispecElement;
import metaModel.types.SimpleType;
import visitor.config.TypeMapping;
import visitor.config.TypeMappingConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * Collecteur d'imports pour les types simples.
 * Utilise un fichier de configuration XML pour déterminer les imports nécessaires.
 */
public class SimpleTypeImportCollector implements ImportCollector {
    
    private final TypeMappingConfig config;
    
    public SimpleTypeImportCollector(TypeMappingConfig config) {
        this.config = config;
    }
    
    @Override
    public boolean canHandle(MinispecElement element) {
        if (element instanceof Attribute) {
            Attribute attr = (Attribute) element;
            return attr.getType() instanceof SimpleType;
        }
        return false;
    }
    
    @Override
    public Set<String> collectImports(MinispecElement element) {
        Set<String> imports = new HashSet<>();
        
        if (element instanceof Attribute) {
            Attribute attr = (Attribute) element;
            if (attr.getType() instanceof SimpleType) {
                SimpleType simpleType = (SimpleType) attr.getType();
                String typeName = simpleType.getTypeName();
                
                // Récupérer le mapping depuis la configuration
                TypeMapping mapping = config.getMapping(typeName);
                if (mapping != null) {
                    imports.addAll(mapping.getRequiredImports());
                }
            }
        }
        
        return imports;
    }
}
