package xmlio.metaModelCreator.minispec;

import metaModel.MetaModelElement;
import metaModel.minispec.Entity;
import metaModel.minispec.MinispecElement;
import metaModel.minispec.types.ReferenceType;
import metaModel.minispec.types.ResolvedReference;
import metaModel.minispec.types.UnresolvedReference;
import org.w3c.dom.Element;
import xmlio.metaModelCreator.CreationContext;
import xmlio.metaModelCreator.ElementCreator;

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
            MetaModelElement target = context.getOrCreateElement(targetEntityId);

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
