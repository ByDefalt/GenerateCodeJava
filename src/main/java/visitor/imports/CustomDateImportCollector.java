package visitor.imports;

import metaModel.Attribute;
import metaModel.MinispecElement;
import metaModel.types.SimpleType;

import java.util.HashSet;
import java.util.Set;

/**
 * OBSOLÈTE: Ce collecteur est maintenant remplacé par le système de configuration TypeMappingConfig.
 * 
 * Au lieu de créer un collecteur personnalisé pour chaque type,
 * ajoutez simplement une entrée dans le fichier de configuration XML:
 * 
 * <primitive name="Date" type="Date" package="java.util.Date"/>
 * 
 * Cette classe est conservée pour compatibilité mais ne devrait plus être utilisée.
 * 
 * @deprecated Utilisez TypeMappingConfig à la place
 */
@Deprecated
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
