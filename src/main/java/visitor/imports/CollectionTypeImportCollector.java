package visitor.imports;

import metaModel.Attribute;
import metaModel.MinispecElement;
import metaModel.types.*;
import visitor.config.TypeMapping;

import java.util.HashSet;
import java.util.Set;

/**
 * Collecte les imports nécessaires pour les types de collection
 */
public class CollectionTypeImportCollector implements ImportCollector {
    
    private final ImportCollectorRegistry registry;
    
    public CollectionTypeImportCollector() {
        this.registry = null;
    }
    
    public CollectionTypeImportCollector(ImportCollectorRegistry registry) {
        this.registry = registry;
    }
    
    @Override
    public boolean canHandle(MinispecElement element) {
        if (element instanceof Attribute) {
            Type type = ((Attribute) element).getType();
            return type instanceof CollectionType;
        }
        return false;
    }
    
    @Override
    public Set<String> collectImports(MinispecElement element) {
        Set<String> imports = new HashSet<>();
        
        if (element instanceof Attribute) {
            Type type = ((Attribute) element).getType();
            collectImportsFromType(type, imports);
        }
        
        return imports;
    }
    
    /**
     * Collecte récursivement les imports d'un type
     */
    private void collectImportsFromType(Type type, Set<String> imports) {
        if (type instanceof ListType) {
            imports.add("java.util.List");
            imports.add("java.util.ArrayList");
            collectImportsFromElementType(((ListType) type).getElementType(), imports);
            
        } else if (type instanceof SetType) {
            imports.add("java.util.Set");
            imports.add("java.util.HashSet");
            collectImportsFromElementType(((SetType) type).getElementType(), imports);
            
        } else if (type instanceof BagType) {
            imports.add("java.util.List");
            imports.add("java.util.ArrayList");
            collectImportsFromElementType(((BagType) type).getElementType(), imports);
            
        } else if (type instanceof ArrayType) {
            // Les arrays n'ont pas besoin d'import mais leurs éléments peuvent en avoir
            collectImportsFromElementType(((ArrayType) type).getElementType(), imports);
        }
    }
    
    /**
     * Collecte les imports du type d'élément
     */
    private void collectImportsFromElementType(Type elementType, Set<String> imports) {
        if (elementType instanceof CollectionType) {
            // Collection imbriquée
            collectImportsFromType(elementType, imports);
        } else if (elementType instanceof SimpleType && registry != null) {
            // Type simple - utiliser le registry si disponible
            SimpleType simpleType = (SimpleType) elementType;
            TypeMapping mapping = registry.getConfig().getMapping(simpleType.getTypeName());
            if (mapping != null) {
                imports.addAll(mapping.getRequiredImports());
            }
        }
        // Pour ReferenceType, pas d'import nécessaire (même package)
    }
}
