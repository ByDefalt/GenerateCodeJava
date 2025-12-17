package xmlio.metaModelCreator.ConfigSpec;

import metaModel.MetaModelElement;
import metaModel.configMetaModel.java.JavaMetaModelConfiguration;
import metaModel.minispec.Entity;
import metaModel.minispec.MinispecElement;
import org.w3c.dom.Element;
import xmlio.metaModelCreator.CreationContext;
import xmlio.metaModelCreator.ElementCreator;

import java.util.List;

public class JavaMetaModelConfigurationCreator implements ElementCreator<JavaMetaModelConfiguration> {
    @Override
    public boolean canHandle(Element xmlElement) {
        return "java-code".equals(xmlElement.getTagName());
    }

    @Override
    public JavaMetaModelConfiguration create(Element xmlElement, CreationContext context) {
        return new JavaMetaModelConfiguration();
    }

    @Override
    public void fillDetails(JavaMetaModelConfiguration element, Element xmlElement, CreationContext context) {
        List<Element> modelElements = context.findChildElements(xmlElement.getAttribute("id"), "model", "code");
        List<Element> primitiveElements = context.findChildElements(xmlElement.getAttribute("id"), "primitive", "code");

        for (Element childEl : modelElements) {
            String childId = childEl.getAttribute("id");
            MetaModelElement childEntity = context.getOrCreateElement(childId);
        }
        for (Element childEl : primitiveElements) {
            String childId = childEl.getAttribute("id");
            MetaModelElement childEntity = context.getOrCreateElement(childId);
        }
    }
}
