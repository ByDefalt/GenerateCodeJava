package visitor.imports;

import metaModel.Attribute;
import metaModel.MinispecElement;
import metaModel.types.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Collecte les imports nécessaires pour les types de collection
 */
public class CollectionTypeImportCollector implements ImportCollector {
    
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
            collectImportsFromType(((ListType) type).getElementType(), imports);
            
        } else if (type instanceof SetType) {
            imports.add("java.util.Set");
            imports.add("java.util.HashSet");
            collectImportsFromType(((SetType) type).getElementType(), imports);
            
        } else if (type instanceof BagType) {
            imports.add("java.util.List");
            imports.add("java.util.ArrayList");
            collectImportsFromType(((BagType) type).getElementType(), imports);
            
        } else if (type instanceof ArrayType) {
            // Les arrays n'ont pas besoin d'import
            collectImportsFromType(((ArrayType) type).getElementType(), imports);
        }
    }
}
