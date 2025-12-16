package xmlio.metaModelCreator;

import metaModel.Entity;
import metaModel.MinispecElement;
import metaModel.types.ReferenceType;
import metaModel.types.ResolvedReference;
import metaModel.types.UnresolvedReference;
import org.w3c.dom.Element;

/**
 * Créateur spécialisé pour les éléments Reference
 */
public class ReferenceCreator implements ElementCreator<ReferenceType> {
    
    @Override
    public boolean canHandle(Element xmlElement) {
        return "Reference".equals(xmlElement.getTagName());
    }
    
    @Override
    public ReferenceType create(Element xmlElement, CreationContext context) {
        String targetEntityId = xmlElement.getAttribute("entity");
        String name = xmlElement.getAttribute("name");
        String refName = !name.isEmpty() ? name : "Unknown";

        if (targetEntityId.startsWith("#")) {
            MinispecElement target = context.getOrCreateElement(targetEntityId);

            if (target != null) {
                return new ResolvedReference(refName, (Entity) target);
            } else {
                context.reportError("La Reference '" + refName + "' pointe vers l'entité '" 
                        + targetEntityId + "' qui n'existe pas ou contient une erreur.");
            }
        } else {
            context.reportError("La Reference '" + refName + "' n'a pas d'attribut 'entity' valide.");
        }

        return new UnresolvedReference(refName);
    }
    
    @Override
    public void fillDetails(ReferenceType element, Element xmlElement, CreationContext context) {
        // Les références n'ont pas de détails à remplir après création
    }
}
