package main;

import metaModel.Model;
import visitor.CodeGenVisitor;
import visitor.config.TypeMappingConfig;
import visitor.imports.ImportCollectorRegistry;
import visitor.java.JavaImportsVisitor;
import visitor.java.JavaVisitor;
import xmlio.metaModelCreator.XMLAnalyser;

/**
 * Exemple d'utilisation du système d'imports avec configuration XML.
 * 
 * Le système charge les mappings de types depuis un fichier de configuration XML,
 * ce qui permet d'ajouter de nouveaux types sans modifier le code (OCP).
 */
public class MainWithImports {
    public static void main(String[] args) {
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromFilenamed("src/main/resources/exempleWithCollections.xml");

        if (model != null) {
            // Charger la configuration des types depuis XML
            TypeMappingConfig config = new TypeMappingConfig();
            config.loadFromXml("src/main/resources/type-mapping-config.xml");
            
            // Afficher la configuration chargée (optionnel, pour debug)
            System.out.println("=== Configuration chargée ===");
            config.printConfiguration();
            System.out.println();
            
            // Créer le visiteur de base
            CodeGenVisitor baseVisitor = new JavaVisitor();

            // Le décorer avec la gestion des imports (qui utilise la config)
            ImportCollectorRegistry registry = new ImportCollectorRegistry(config);
            JavaImportsVisitor decoratedVisitor = new JavaImportsVisitor(baseVisitor, registry);

            // Générer le code Java avec imports
            model.accept(decoratedVisitor);
            System.out.println("=== Code généré avec imports ===");
            System.out.println(decoratedVisitor.getResult());
        } else {
            System.out.println("Erreur: impossible de charger le modèle");
        }
    }
}
