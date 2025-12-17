package xmlio.metaModelCreator.minispec;

import metaModel.MetaModelElement;
import metaModel.minispec.Attribute;
import metaModel.minispec.Entity;
import metaModel.minispec.MinispecElement;
import org.w3c.dom.Element;
import xmlio.metaModelCreator.CreationContext;
import xmlio.metaModelCreator.ElementCreator;

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

        if (!extendId.isEmpty()) {
            if (extendId.startsWith("#")) {
                MetaModelElement parentObj = context.getOrCreateElement(extendId);

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
            MetaModelElement attr = context.getOrCreateElement(childId);
            if (attr != null) {
                entity.getAttributes().add((Attribute) attr);
            }
        }
    }
}
