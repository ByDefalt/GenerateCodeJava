package visitor.imports;

import metaModel.MinispecElement;

import java.util.*;

/**
 * Registre des collecteurs d'imports.
 * Permet d'ajouter dynamiquement de nouveaux collecteurs (OCP).
 */
public class ImportCollectorRegistry {
    
    private final List<ImportCollector> collectors;
    
    public ImportCollectorRegistry() {
        this.collectors = new ArrayList<>();
        registerDefaultCollectors();
    }
    
    /**
     * Enregistre les collecteurs par défaut
     */
    private void registerDefaultCollectors() {
        register(new CollectionTypeImportCollector());
        register(new ReferenceTypeImportCollector());
    }
    
    /**
     * Enregistre un nouveau collecteur
     */
    public void register(ImportCollector collector) {
        collectors.add(collector);
    }
    
    /**
     * Collecte tous les imports pour un élément
     */
    public Set<String> collectImports(MinispecElement element) {
        Set<String> imports = new HashSet<>();
        
        for (ImportCollector collector : collectors) {
            if (collector.canHandle(element)) {
                imports.addAll(collector.collectImports(element));
            }
        }
        
        return imports;
    }
    
    /**
     * Collecte tous les imports pour une collection d'éléments
     */
    public Set<String> collectImports(Collection<? extends MinispecElement> elements) {
        Set<String> imports = new HashSet<>();
        
        for (MinispecElement element : elements) {
            imports.addAll(collectImports(element));
        }
        
        return imports;
    }
}
