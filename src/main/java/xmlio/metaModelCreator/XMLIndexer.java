package xmlio.metaModelCreator;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Responsable de l'indexation des éléments XML par leur ID.
 */
public class XMLIndexer {
    
    private final XMLAnalyserContext context;
    
    public XMLIndexer(XMLAnalyserContext context) {
        this.context = context;
    }
    
    /**
     * Indexe récursivement tous les éléments XML qui ont un attribut "id"
     */
    public void indexElements(Element root) {
        indexElementRecursive(root);
    }
    
    private void indexElementRecursive(Element element) {
        if (element.hasAttribute("id")) {
            String id = element.getAttribute("id");
            context.indexXmlElement(id, element);
        }
        
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element) {
                indexElementRecursive((Element) children.item(i));
            }
        }
    }
}
