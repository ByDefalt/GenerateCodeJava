package main;

import metaModel.Model;
import visitor.CodeGenVisitor;
import visitor.imports.CustomDateImportCollector;
import visitor.java.JavaImportsVisitor;
import visitor.java.JavaVisitor;
import xmlio.metaModelCreator.XMLAnalyser;

/**
 * Exemple d'utilisation du décorateur d'imports avec OCP.
 * Le décorateur réutilise JavaVisitor existant sans duplication de code.
 */
public class MainWithImports {
    public static void main(String[] args) {
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromFilenamed("src/main/resources/exempleWithCollections.xml");

        if (model != null) {
            // Créer le visiteur de base
            CodeGenVisitor baseVisitor = new JavaVisitor();

            // Le décorer avec la gestion des imports
            JavaImportsVisitor decoratedVisitor =
                    new JavaImportsVisitor(baseVisitor);

            // Optionnel : ajouter des collecteurs personnalisés
            decoratedVisitor.getImportRegistry().register(new CustomDateImportCollector());

            // Générer le code Java avec imports
            model.accept(decoratedVisitor);
            System.out.println(decoratedVisitor.getResult());
        } else {
            System.out.println("Erreur: impossible de charger le modèle");
        }
    }
}