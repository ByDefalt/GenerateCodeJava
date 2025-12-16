package visitor.imports;

import metaModel.MinispecElement;

import java.util.Set;

/**
 * Interface pour collecter les imports nécessaires d'un élément du métamodèle.
 * Respecte le principe OCP : pour ajouter un nouveau type nécessitant des imports,
 * créer une nouvelle implémentation sans modifier le code existant.
 */
public interface ImportCollector {
    
    /**
     * Vérifie si ce collecteur peut gérer l'élément donné
     */
    boolean canHandle(MinispecElement element);
    
    /**
     * Collecte les imports nécessaires pour cet élément
     * @return Set des imports complets (ex: "java.util.List")
     */
    Set<String> collectImports(MinispecElement element);
}
