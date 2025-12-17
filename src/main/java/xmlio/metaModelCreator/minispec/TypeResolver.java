package xmlio.metaModelCreator.minispec;

import metaModel.MetaModelElement;
import metaModel.minispec.Entity;
import metaModel.minispec.MinispecElement;
import metaModel.minispec.types.*;
import org.w3c.dom.Element;
import xmlio.metaModelCreator.CreationContext;

/**
 * Résout les types à partir de chaînes de caractères ou d'éléments XML.
 * Gère les types primitifs, les références et les collections.
 */
public class TypeResolver {

    private final CreationContext context;
    
    public TypeResolver(CreationContext context) {
        this.context = context;
    }
    
    /**
     * Résout un type à partir d'une chaîne (nom de type ou ID)
     */
    public Type resolveType(String typeStr) {
        if (typeStr == null || typeStr.isEmpty()) {
            return new SimpleType("void");
        }

        // Type référencé par ID
        if (typeStr.startsWith("#")) {
            MetaModelElement el = context.getOrCreateElement(typeStr);

            if (el instanceof Type) {
                return (Type) el;
            } else if (el == null) {
                context.reportError("Type introuvable ou erreur de cycle sur l'ID '" + typeStr + "'.");
                return new SimpleType("Error_NotFound");
            } else {
                if (el instanceof Entity) {
                    context.reportError("L'attribut utilise directement l'entité '" + typeStr 
                            + "' comme type. Utilisez <Reference>.");
                    return new UnresolvedReference(((Entity) el).getName());
                }
                context.reportError("L'ID '" + typeStr + "' ne correspond pas à un Type valide.");
                return new SimpleType("Error_InvalidType");
            }
        }
        
        // Type primitif ou non résolu
        return new SimpleType(typeStr);
    }
    
    /**
     * Crée un type de collection à partir d'un élément XML
     */
    public Type createCollectionType(Element xmlElement, String tagName) {
        String ofAttr = xmlElement.getAttribute("of");
        String refAttr = xmlElement.getAttribute("ref");

        Type elementType;
        if (!refAttr.isEmpty()) {
            elementType = resolveType(refAttr);
        } else {
            elementType = resolveType(ofAttr);
        }

        Integer min = parseIntSafe(xmlElement.getAttribute("min"));
        Integer max = parseIntSafe(xmlElement.getAttribute("max"));
        Integer size = parseIntSafe(xmlElement.getAttribute("size"));
        if (size == null) size = 0;

        return switch (tagName) {
            case "Array" -> new ArrayType(elementType, size);
            case "List" -> new ListType(elementType, min, max);
            case "Set" -> new SetType(elementType, min, max);
            case "Bag" -> new BagType(elementType, min, max);
            default -> new SimpleType("UnknownCollection");
        };
    }

    private Integer parseIntSafe(String s) {
        try {
            return (s != null && !s.isEmpty()) ? Integer.parseInt(s) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
