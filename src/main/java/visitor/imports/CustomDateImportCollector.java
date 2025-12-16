package visitor.imports;

import metaModel.Attribute;
import metaModel.MinispecElement;
import metaModel.types.SimpleType;

import java.util.HashSet;
import java.util.Set;

/**
 * Exemple de collecteur d'imports personnalisé pour les types primitifs spéciaux.
 * Montre comment étendre le système sans modifier le code existant (OCP).
 */
public class CustomDateImportCollector implements ImportCollector {
    
    @Override
    public boolean canHandle(MinispecElement element) {
        if (element instanceof Attribute) {
            Attribute attr = (Attribute) element;
            if (attr.getType() instanceof SimpleType) {
                SimpleType simpleType = (SimpleType) attr.getType();
                return "Date".equals(simpleType.getTypeName());
            }
        }
        return false;
    }
    
    @Override
    public Set<String> collectImports(MinispecElement element) {
        Set<String> imports = new HashSet<>();
        imports.add("java.util.Date");
        return imports;
    }
}
