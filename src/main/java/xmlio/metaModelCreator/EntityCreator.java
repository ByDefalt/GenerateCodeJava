package xmlio.metaModelCreator;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.MinispecElement;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Créateur spécialisé pour les éléments Entity
 */
public class EntityCreator implements ElementCreator<Entity> {
    
    @Override
    public boolean canHandle(Element xmlElement) {
        return "Entity".equals(xmlElement.getTagName());
    }
    
    @Override
    public Entity create(Element xmlElement, CreationContext context) {
        String name = xmlElement.getAttribute("name");
        String extendId = xmlElement.getAttribute("extend");
        Entity superEntity = null;

        if (extendId != null && !extendId.isEmpty()) {
            if (extendId.startsWith("#")) {
                MinispecElement parentObj = context.getOrCreateElement(extendId);

                if (parentObj != null) {
                    superEntity = (Entity) parentObj;
                } else {
                    context.reportError("Impossible de résoudre l'héritage pour '" + name 
                            + "'. (ID inexistant ou cycle détecté sur " + extendId + ")");
                }
            } else {
                context.reportError("Attribut 'extend' invalide pour l'entité '" + name 
                        + "'. Doit être un ID (#...), reçu : " + extendId);
            }
        }

        return new Entity(name, superEntity, new ArrayList<>());
    }
    
    @Override
    public void fillDetails(Entity entity, Element xmlElement, CreationContext context) {
        String entityId = xmlElement.getAttribute("id");
        
        List<Element> childElements = context.findChildElements(entityId, "Attribute", "entity");
        
        for (Element childEl : childElements) {
            String childId = childEl.getAttribute("id");
            MinispecElement attr = context.getOrCreateElement(childId);
            if (attr != null) {
                entity.getAttributes().add((Attribute) attr);
            }
        }
    }
}
