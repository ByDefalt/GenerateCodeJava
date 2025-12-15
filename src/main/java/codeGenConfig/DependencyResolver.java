package codeGenConfig;

import metaModel.*;

import java.util.*;

/**
 * Résolveur de dépendances entre modèles et entités
 * Construit un graphe de dépendances et calcule les imports nécessaires
 */
public class DependencyResolver {

    private CodeGenConfig config;

    public DependencyResolver(CodeGenConfig config) {
        this.config = config;
    }

    /**
     * Analyse un modèle et retourne un mapping des entités vers leurs dépendances
     */
    public Map<String, Set<String>> buildDependencyGraph(Model model) {
        Map<String, Set<String>> dependencies = new HashMap<>();

        if (model.getEntities() == null) {
            return dependencies;
        }

        // Créer un index des entités par nom
        Map<String, Entity> entityIndex = new HashMap<>();
        for (Entity entity : model.getEntities()) {
            entityIndex.put(entity.getName(), entity);
            dependencies.put(entity.getName(), new HashSet<>());
        }

        // Analyser chaque entité
        for (Entity entity : model.getEntities()) {
            Set<String> entityDeps = dependencies.get(entity.getName());

            // Dépendance d'héritage
            if (entity.hasSuperType()) {
                entityDeps.add(entity.getSuperType());
            }

            // Dépendances des attributs
            if (entity.getAttributes() != null) {
                for (Attribute attr : entity.getAttributes()) {
                    Type type = attr.getType();
                    String baseType = type.getBaseType();

                    // Si le type de base est une entité du modèle, ajouter la dépendance
                    if (entityIndex.containsKey(baseType)) {
                        entityDeps.add(baseType);
                    }
                }
            }
        }

        return dependencies;
    }

    /**
     * Calcule tous les imports nécessaires pour une entité donnée
     */
    public Set<String> computeImportsForEntity(Entity entity, Model model) {
        Set<String> imports = new HashSet<>();

        if (entity.getAttributes() == null) {
            return imports;
        }

        // Créer un index des entités du modèle
        Set<String> entitiesInModel = new HashSet<>();
        if (model.getEntities() != null) {
            for (Entity e : model.getEntities()) {
                entitiesInModel.add(e.getName());
            }
        }

        String currentModelPackage = config.getPackageForModel(model.getName());

        // Analyser chaque attribut
        for (Attribute attr : entity.getAttributes()) {
            Type type = attr.getType();

            // Import pour le type collection
            if (type.isCollection()) {
                String collectionType = type.getCollectionType();
                String collectionImport = config.getImportForPrimitive(collectionType);
                if (collectionImport != null) {
                    imports.add(collectionImport);
                }
            }

            // Import pour le type de base
            String baseType = type.getBaseType();

            // Si c'est un type primitif avec import
            String primitiveImport = config.getImportForPrimitive(baseType);
            if (primitiveImport != null) {
                imports.add(primitiveImport);
            }
            // Si c'est une entité d'un autre modèle
            else if (!entitiesInModel.contains(baseType)) {
                // Chercher dans quel modèle se trouve cette entité
                // Pour l'instant, on suppose que toutes les entités sont dans le même modèle
                // ou que les imports inter-modèles sont gérés différemment
            }
        }

        // Import pour le supertype si nécessaire
        if (entity.hasSuperType()) {
            String superType = entity.getSuperType();
            // Si le supertype n'est pas dans le modèle courant, il faut l'importer
            if (!entitiesInModel.contains(superType)) {
                // Trouver le package du supertype
                // Pour l'instant, on suppose qu'il est dans le même package
            }
        }

        return imports;
    }

    /**
     * Vérifie s'il y a des cycles dans les dépendances
     */
    public List<String> detectCyclicDependencies(Map<String, Set<String>> dependencies) {
        List<String> cycles = new ArrayList<>();

        for (String entity : dependencies.keySet()) {
            Set<String> visited = new HashSet<>();
            List<String> path = new ArrayList<>();

            if (hasCycle(entity, dependencies, visited, path)) {
                cycles.add("Cyclic dependency detected: " + String.join(" -> ", path));
            }
        }

        return cycles;
    }

    private boolean hasCycle(String entity, Map<String, Set<String>> dependencies,
                             Set<String> visited, List<String> path) {
        if (path.contains(entity)) {
            path.add(entity);
            return true;
        }

        if (visited.contains(entity)) {
            return false;
        }

        visited.add(entity);
        path.add(entity);

        Set<String> deps = dependencies.get(entity);
        if (deps != null) {
            for (String dep : deps) {
                if (hasCycle(dep, dependencies, visited, new ArrayList<>(path))) {
                    return true;
                }
            }
        }

        path.remove(path.size() - 1);
        return false;
    }

    /**
     * Calcule l'ordre topologique des entités (pour la génération dans le bon ordre)
     */
    public List<String> topologicalSort(Map<String, Set<String>> dependencies) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();

        for (String entity : dependencies.keySet()) {
            if (!visited.contains(entity)) {
                topologicalSortUtil(entity, dependencies, visited, visiting, result);
            }
        }

        Collections.reverse(result);
        return result;
    }

    private void topologicalSortUtil(String entity, Map<String, Set<String>> dependencies,
                                     Set<String> visited, Set<String> visiting, List<String> result) {
        if (visiting.contains(entity)) {
            return; // Cycle détecté, on arrête
        }

        if (visited.contains(entity)) {
            return;
        }

        visiting.add(entity);

        Set<String> deps = dependencies.get(entity);
        if (deps != null) {
            for (String dep : deps) {
                topologicalSortUtil(dep, dependencies, visited, visiting, result);
            }
        }

        visiting.remove(entity);
        visited.add(entity);
        result.add(entity);
    }
}