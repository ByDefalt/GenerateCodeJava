package visitor.java;

import metaModel.*;
import metaModel.types.*;
import visitor.CodeGenVisitor;
import visitor.Visitor;
import visitor.imports.ImportCollectorRegistry;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Décorateur qui ajoute la gestion des imports au JavaVisitor existant.
 * Évite la duplication de code en réutilisant JavaVisitor (Decorator Pattern).
 *
 * Utilise maintenant un système de configuration XML pour les mappings de types.
 *
 * Usage:
 * TypeMappingConfig config = new TypeMappingConfig();
 * config.loadFromXml("config/type-mapping.xml");
 * ImportCollectorRegistry registry = new ImportCollectorRegistry(config);
 * JavaVisitor baseVisitor = new JavaVisitor();
 * JavaImportsVisitor visitor = new JavaImportsVisitor(baseVisitor, registry);
 * model.accept(visitor);
 * String code = visitor.getResult();
 */
public class JavaImportsVisitor extends Visitor {

    private final CodeGenVisitor delegate;
    private final ImportCollectorRegistry importRegistry;
    private final Map<String, Set<String>> entityImports; // entityName -> imports

    /**
     * Constructeur avec visiteur délégué et registry personnalisé
     */
    public JavaImportsVisitor(CodeGenVisitor delegate, ImportCollectorRegistry importRegistry) {
        this.delegate = delegate;
        this.importRegistry = importRegistry;
        this.entityImports = new HashMap<>();
    }

    /**
     * Constructeur avec visiteur délégué et registry par défaut
     */
    public JavaImportsVisitor(CodeGenVisitor delegate) {
        this(delegate, new ImportCollectorRegistry());
    }

    /**
     * Constructeur par défaut
     */
    public JavaImportsVisitor() {
        this(new JavaVisitor());
    }

    public ImportCollectorRegistry getImportRegistry() {
        return importRegistry;
    }

    @Override
    public void visitModel(Model e) {
        entityImports.clear();
        delegate.visitModel(e);
    }

    @Override
    public void visitEntity(Entity e) {
        // Collecter les imports pour cette entité avant de la visiter
        Set<String> imports = new TreeSet<>();
        if (e.getAttributes() != null) {
            for (Attribute attr : e.getAttributes()) {
                imports.addAll(importRegistry.collectImports(attr));
            }
        }
        entityImports.put(e.getName(), imports);

        delegate.visitEntity(e);
    }

    @Override
    public void visitAttribute(Attribute e) {
        delegate.visitAttribute(e);
    }

    @Override
    public void visitSimpleType(SimpleType e) {
        delegate.visitSimpleType(e);
    }

    @Override
    public void visitResolvedReference(ResolvedReference e) {
        delegate.visitResolvedReference(e);
    }

    @Override
    public void visitUnresolvedReference(UnresolvedReference e) {
        delegate.visitUnresolvedReference(e);
    }

    @Override
    public void visitArrayType(ArrayType e) {
        delegate.visitArrayType(e);
    }

    @Override
    public void visitListType(ListType e) {
        delegate.visitListType(e);
    }

    @Override
    public void visitSetType(SetType e) {
        delegate.visitSetType(e);
    }

    @Override
    public void visitBagType(BagType e) {
        delegate.visitBagType(e);
    }

    public String getResult() {
        return injectImports(delegate.getResult());
    }

    /**
     * Injecte les imports dans le code généré par le délégué
     */
    private String injectImports(String generatedCode) {
        if (entityImports.isEmpty()) {
            return generatedCode;
        }

        StringBuilder result = new StringBuilder();
        String[] classes = generatedCode.split("(?=public class )");

        for (String classCode : classes) {
            if (classCode.trim().isEmpty()) continue;

            // Extraire le nom de la classe
            Pattern pattern = Pattern.compile("public class (\\w+)");
            Matcher matcher = pattern.matcher(classCode);

            if (matcher.find()) {
                String className = matcher.group(1);
                Set<String> imports = entityImports.get(className);

                if (imports != null && !imports.isEmpty()) {
                    // Ajouter les imports avant la déclaration de classe
                    for (String importStr : imports) {
                        result.append("import ").append(importStr).append(";\n");
                    }
                    result.append("\n");
                }
            }

            result.append(classCode);
        }

        return result.toString();
    }
}