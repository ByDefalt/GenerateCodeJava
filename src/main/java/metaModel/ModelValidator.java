package metaModel;

import java.util.*;

public class ModelValidator {

    /**
     * Valide le modèle et retourne la liste des erreurs trouvées
     */
    public static List<String> validate(Model model) {
        List<String> errors = new ArrayList<>();

        if (model == null || model.getEntities() == null) {
            return errors;
        }

        // Créer un index des entités par nom
        Map<String, Entity> entityIndex = new HashMap<>();
        for (Entity entity : model.getEntities()) {
            entityIndex.put(entity.getName(), entity);
        }

        // Vérifier chaque entité
        for (Entity entity : model.getEntities()) {
            // Vérifier la circularité de l'héritage
            List<String> circularityError = checkCircularInheritance(entity, entityIndex);
            if (circularityError != null) {
                errors.addAll(circularityError);
            }

            // Vérifier les définitions multiples d'attributs
            List<String> duplicateErrors = checkDuplicateAttributes(entity, entityIndex);
            if (duplicateErrors != null) {
                errors.addAll(duplicateErrors);
            }

            // Vérifier que le supertype existe
            if (entity.hasSuperType()) {
                if (!entityIndex.containsKey(entity.getSuperType())) {
                    errors.add("Entity '" + entity.getName() + "' inherits from unknown entity '" + entity.getSuperType() + "'");
                }
            }
        }

        return errors;
    }

    /**
     * Vérifie s'il y a une circularité dans l'héritage
     * Retourne null si pas de circularité, sinon retourne la liste des erreurs
     */
    private static List<String> checkCircularInheritance(Entity entity, Map<String, Entity> entityIndex) {
        Set<String> visited = new HashSet<>();
        Entity current = entity;
        List<String> path = new ArrayList<>();

        while (current != null && current.hasSuperType()) {
            path.add(current.getName());

            if (visited.contains(current.getName())) {
                // Circularité détectée
                List<String> errors = new ArrayList<>();

                // Construire la chaîne à partir du point de circularité
                int circleStart = path.indexOf(current.getName());
                StringBuilder chain = new StringBuilder();
                for (int i = circleStart; i < path.size(); i++) {
                    chain.append(path.get(i)).append(" -> ");
                }
                chain.append(current.getName()); // Fermer la boucle

                errors.add("Circular inheritance detected: " + chain.toString());
                return errors;
            }

            visited.add(current.getName());
            current = entityIndex.get(current.getSuperType());
        }

        return null;
    }

    /**
     * Construit la chaîne d'héritage pour l'affichage (NON UTILISÉE maintenant)
     */
    private static String buildInheritanceChain(Entity entity, Map<String, Entity> entityIndex) {
        StringBuilder chain = new StringBuilder();
        Entity current = entity;
        Set<String> visited = new HashSet<>();

        while (current != null) {
            if (visited.contains(current.getName())) {
                // Éviter la boucle infinie
                chain.append(current.getName()).append(" (circular)");
                break;
            }

            visited.add(current.getName());
            chain.append(current.getName());

            if (current.hasSuperType()) {
                chain.append(" -> ");
                current = entityIndex.get(current.getSuperType());
            } else {
                break;
            }
        }

        return chain.toString();
    }

    /**
     * Vérifie s'il y a des définitions multiples d'attributs dans la hiérarchie
     */
    private static List<String> checkDuplicateAttributes(Entity entity, Map<String, Entity> entityIndex) {
        List<String> errors = new ArrayList<>();

        // Collecter tous les noms d'attributs de cette entité
        Set<String> localAttributes = new HashSet<>();
        if (entity.getAttributes() != null) {
            for (Attribute attr : entity.getAttributes()) {
                localAttributes.add(attr.getName());
            }
        }

        // Collecter tous les noms d'attributs des entités parentes
        // Utiliser un Set pour détecter les cycles
        Set<String> visitedEntities = new HashSet<>();
        visitedEntities.add(entity.getName());

        Entity current = entity;

        while (current != null && current.hasSuperType()) {
            String superTypeName = current.getSuperType();

            // Vérifier la circularité
            if (visitedEntities.contains(superTypeName)) {
                // Circularité détectée, arrêter la vérification
                // (l'erreur sera déjà rapportée par checkCircularInheritance)
                break;
            }

            visitedEntities.add(superTypeName);
            current = entityIndex.get(superTypeName);

            if (current != null && current.getAttributes() != null) {
                for (Attribute attr : current.getAttributes()) {
                    String attrName = attr.getName();
                    if (localAttributes.contains(attrName)) {
                        errors.add("Entity '" + entity.getName() + "' redefines attribute '" + attrName +
                                "' from parent entity '" + current.getName() + "'");
                    }
                }
            }
        }

        return errors.isEmpty() ? null : errors;
    }

    /**
     * Retourne tous les attributs d'une entité, incluant ceux hérités
     */
    public static List<Attribute> getAllAttributes(Entity entity, Map<String, Entity> entityIndex) {
        List<Attribute> allAttributes = new ArrayList<>();
        Set<String> visitedEntities = new HashSet<>();

        collectAttributesRecursive(entity, entityIndex, allAttributes, visitedEntities);

        return allAttributes;
    }

    /**
     * Méthode récursive pour collecter les attributs avec protection contre les cycles
     */
    private static void collectAttributesRecursive(Entity entity, Map<String, Entity> entityIndex,
                                                   List<Attribute> allAttributes, Set<String> visitedEntities) {
        if (entity == null || visitedEntities.contains(entity.getName())) {
            return;
        }

        visitedEntities.add(entity.getName());

        // Collecter les attributs des parents d'abord (pour respecter l'ordre d'héritage)
        if (entity.hasSuperType()) {
            Entity parent = entityIndex.get(entity.getSuperType());
            if (parent != null) {
                collectAttributesRecursive(parent, entityIndex, allAttributes, visitedEntities);
            }
        }

        // Ajouter les attributs locaux
        if (entity.getAttributes() != null) {
            allAttributes.addAll(entity.getAttributes());
        }
    }
}