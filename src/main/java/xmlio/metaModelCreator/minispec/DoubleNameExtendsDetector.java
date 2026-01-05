package xmlio.metaModelCreator.minispec;

import metaModel.minispec.Attribute;
import metaModel.minispec.Entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Détecte les conflits de noms d'attributs entre une entité et ses parents dans la hiérarchie d'héritage.
 * Vérifie qu'une entité n'a pas un attribut avec le même nom qu'un attribut d'une entité dont elle hérite.
 */
public class DoubleNameExtendsDetector {

    private final Set<String> checkedEntities;

    public DoubleNameExtendsDetector() {
        this.checkedEntities = new HashSet<>();
    }

    /**
     * Vérifie qu'une entité n'a pas d'attributs avec le même nom que ceux de ses parents
     * @param entity L'entité à vérifier
     * @return true si aucun conflit n'est détecté, false sinon
     */
    public boolean checkEntity(Entity entity) {
        if (entity == null || entity.getAttributes() == null) {
            return true;
        }

        // Si déjà vérifié, on évite les vérifications redondantes
        String entityKey = getEntityKey(entity);
        if (checkedEntities.contains(entityKey)) {
            return true;
        }

        // Vérifier chaque attribut de l'entité
        for (Attribute attribute : entity.getAttributes()) {
            if (!checkAttributeInHierarchy(entity, attribute.getName())) {
                checkedEntities.add(entityKey);
                return false;
            }
        }

        // Marquer comme vérifié
        checkedEntities.add(entityKey);
        return true;
    }

    /**
     * Vérifie si un nom d'attribut existe déjà dans la hiérarchie des parents
     * @param entity L'entité de départ
     * @param attributeName Le nom de l'attribut à vérifier
     * @return true si aucun conflit, false si un parent a un attribut avec le même nom
     */
    private boolean checkAttributeInHierarchy(Entity entity, String attributeName) {
        Entity current = entity.getSuperEntity();

        while (current != null) {
            if (current.getAttributes() != null) {
                for (Attribute parentAttr : current.getAttributes()) {
                    if (parentAttr.getName().equals(attributeName)) {
                        return false; // Conflit détecté
                    }
                }
            }
            current = current.getSuperEntity();
        }

        return true;
    }

    /**
     * Trouve tous les attributs en conflit pour une entité donnée
     * @param entity L'entité à vérifier
     * @return Un ensemble de noms d'attributs en conflit
     */
    public Set<String> findConflictingAttributes(Entity entity) {
        Set<String> conflicts = new HashSet<>();

        if (entity == null || entity.getAttributes() == null) {
            return conflicts;
        }

        for (Attribute attribute : entity.getAttributes()) {
            String conflictingParent = findParentWithAttribute(entity, attribute.getName());
            if (conflictingParent != null) {
                conflicts.add(attribute.getName());
            }
        }

        return conflicts;
    }

    /**
     * Trouve le nom du parent qui possède un attribut avec le nom donné
     * @param entity L'entité de départ
     * @param attributeName Le nom de l'attribut recherché
     * @return Le nom de l'entité parente qui a l'attribut, ou null si aucun conflit
     */
    public String findParentWithAttribute(Entity entity, String attributeName) {
        Entity current = entity.getSuperEntity();

        while (current != null) {
            if (current.getAttributes() != null) {
                for (Attribute parentAttr : current.getAttributes()) {
                    if (parentAttr.getName().equals(attributeName)) {
                        return current.getName();
                    }
                }
            }
            current = current.getSuperEntity();
        }

        return null;
    }

    /**
     * Retourne le chemin complet de la hiérarchie pour une entité
     * @param entity L'entité
     * @return Une chaîne représentant la hiérarchie (ex: "Child -> Parent -> GrandParent")
     */
    public String getHierarchyPath(Entity entity) {
        if (entity == null) {
            return "null";
        }

        StringBuilder path = new StringBuilder(entity.getName());
        Entity current = entity.getSuperEntity();

        while (current != null) {
            path.append(" -> ").append(current.getName());
            current = current.getSuperEntity();
        }

        return path.toString();
    }

    /**
     * Génère un rapport détaillé des conflits d'attributs pour une entité
     * @param entity L'entité à analyser
     * @return Une chaîne décrivant tous les conflits trouvés
     */
    public String getConflictReport(Entity entity) {
        if (entity == null) {
            return "Entité null";
        }

        Set<String> conflicts = findConflictingAttributes(entity);
        if (conflicts.isEmpty()) {
            return "Aucun conflit détecté pour l'entité '" + entity.getName() + "'";
        }

        StringBuilder report = new StringBuilder();
        report.append("Conflits d'attributs détectés pour l'entité '").append(entity.getName()).append("':\n");

        for (String attrName : conflicts) {
            String parentName = findParentWithAttribute(entity, attrName);
            report.append("  - Attribut '").append(attrName)
                    .append("' existe déjà dans la classe parente '").append(parentName).append("'\n");
        }

        report.append("Hiérarchie: ").append(getHierarchyPath(entity));

        return report.toString();
    }

    /**
     * Génère une clé unique pour une entité basée sur son nom et sa hiérarchie
     */
    private String getEntityKey(Entity entity) {
        return entity.getName() + "_" + System.identityHashCode(entity);
    }

    /**
     * Réinitialise le détecteur
     */
    public void reset() {
        checkedEntities.clear();
    }
}