package xmlio.metaModelCreator.ConfigSpec;

import metaModel.configMetaModel.java.ModelConfig;
import metaModel.configMetaModel.java.PrimitiveConfig;
import org.w3c.dom.Element;
import xmlio.metaModelCreator.CreationContext;
import xmlio.metaModelCreator.ElementCreator;

public class PrimitiveConfigCreator implements ElementCreator<PrimitiveConfig> {
    @Override
    public boolean canHandle(Element xmlElement) {
        return "primitive".equals(xmlElement.getTagName());
    }

    @Override
    public PrimitiveConfig create(Element xmlElement, CreationContext context) {
        String name = xmlElement.getAttribute("name");
        String type = xmlElement.getAttribute("type");
        String packageName = xmlElement.getAttribute("package");
        return new PrimitiveConfig(name, type, packageName);
    }

    @Override
    public void fillDetails(PrimitiveConfig element, Element xmlElement, CreationContext context) {
        //rien à faire pour l'instant
    }
}
