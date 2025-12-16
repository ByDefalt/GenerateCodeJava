package visitor.imports;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.MinispecElement;
import metaModel.types.ReferenceType;
import metaModel.types.ResolvedReference;
import metaModel.types.Type;

import java.util.HashSet;
import java.util.Set;

/**
 * Collecte les imports nécessaires pour les types référence
 */
public class ReferenceTypeImportCollector implements ImportCollector {
    
    private final String packageName;
    
    public ReferenceTypeImportCollector() {
        this(""); // Package par défaut (même package)
    }
    
    public ReferenceTypeImportCollector(String packageName) {
        this.packageName = packageName;
    }
    
    @Override
    public boolean canHandle(MinispecElement element) {
        if (element instanceof Attribute) {
            Type type = ((Attribute) element).getType();
            return type instanceof ReferenceType;
        }
        return false;
    }
    
    @Override
    public Set<String> collectImports(MinispecElement element) {
        Set<String> imports = new HashSet<>();
        
        if (element instanceof Attribute) {
            Type type = ((Attribute) element).getType();
            
            if (type instanceof ResolvedReference) {
                ResolvedReference ref = (ResolvedReference) type;
                Entity referencedEntity = ref.getReferencedEntity();
                
                // Si on a un package name, ajouter l'import complet
                if (!packageName.isEmpty()) {
                    imports.add(packageName + "." + referencedEntity.getName());
                }
                // Sinon, pas d'import nécessaire (même package)
            }
        }
        
        return imports;
    }
}
