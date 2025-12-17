package visitor.imports;

import metaModel.MinispecElement;
import visitor.config.TypeMappingConfig;

import java.util.*;

/**
 * Registre des collecteurs d'imports.
 * Permet d'ajouter dynamiquement de nouveaux collecteurs (OCP).
 * Utilise une configuration pour les mappings de types.
 */
public class ImportCollectorRegistry {
    
    private final List<ImportCollector> collectors;
    private final TypeMappingConfig config;
    
    /**
     * Constructeur avec configuration par défaut
     */
    public ImportCollectorRegistry() {
        this(new TypeMappingConfig());
    }
    
    /**
     * Constructeur avec configuration personnalisée
     */
    public ImportCollectorRegistry(TypeMappingConfig config) {
        this.collectors = new ArrayList<>();
        this.config = config;
        registerDefaultCollectors();
    }
    
    /**
     * Enregistre les collecteurs par défaut
     */
    private void registerDefaultCollectors() {
        register(new SimpleTypeImportCollector(config));
        register(new CollectionTypeImportCollector());
        register(new ReferenceTypeImportCollector());
    }
    
    /**
     * Récupère la configuration des types
     */
    public TypeMappingConfig getConfig() {
        return config;
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
