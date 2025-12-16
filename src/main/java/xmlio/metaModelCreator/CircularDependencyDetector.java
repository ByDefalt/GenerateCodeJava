package xmlio.metaModelCreator;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Détecte les dépendances circulaires lors de la création d'éléments.
 * Utilise une pile pour tracer le chemin de création actuel.
 */
public class CircularDependencyDetector {
    
    private final Set<String> currentlyCreating;
    private final Stack<String> creationPath;
    
    public CircularDependencyDetector() {
        this.currentlyCreating = new HashSet<>();
        this.creationPath = new Stack<>();
    }
    
    /**
     * Marque un ID comme étant en cours de création
     * @return true si aucun cycle n'est détecté, false sinon
     */
    public boolean startCreating(String id) {
        if (currentlyCreating.contains(id)) {
            return false; // Cycle détecté
        }
        currentlyCreating.add(id);
        creationPath.push(id);
        return true;
    }
    
    /**
     * Marque un ID comme ayant terminé sa création
     */
    public void finishCreating(String id) {
        currentlyCreating.remove(id);
        if (!creationPath.isEmpty() && creationPath.peek().equals(id)) {
            creationPath.pop();
        }
    }
    
    /**
     * Vérifie si un ID est actuellement en cours de création
     */
    public boolean isCurrentlyCreating(String id) {
        return currentlyCreating.contains(id);
    }
    
    /**
     * Obtient le chemin de dépendance actuel (pour les messages d'erreur détaillés)
     */
    public String getCreationPath() {
        if (creationPath.isEmpty()) {
            return "[]";
        }
        return creationPath.toString();
    }
    
    /**
     * Réinitialise le détecteur
     */
    public void reset() {
        currentlyCreating.clear();
        creationPath.clear();
    }
}
