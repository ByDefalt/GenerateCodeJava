package main;

import XMLIO.XMLAnalyser;
import metaModel.Model;
import metaModel.ModelValidator;
import visitor.JavaVisitor;
import prettyPrinter.PrettyPrinter;

import java.util.List;

public class MainAvecHeritage {
    public static void main(String[] args){
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║        Génération de code avec Héritage et Validation         ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        // Test 1: Héritage valide
        testModel("src/main/resources/Exempleavecheritage.xml", "Héritage Valide");

        System.out.println("\n" + "=".repeat(70) + "\n");

        // Test 2: Circularité
        testModel("src/main/resources/Exemplecircularite.xml", "Erreur de Circularité");

        System.out.println("\n" + "=".repeat(70) + "\n");

        // Test 3: Définition multiple
        testModel("src/main/resources/Exempledefinitionmultiple.xml", "Erreur de Définition Multiple");
    }

    private static void testModel(String filename, String testName) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("TEST: " + testName);
        System.out.println("Fichier: " + filename);
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromFilenamed(filename);

        if (model == null) {
            System.out.println("✗ Erreur: impossible de charger le modèle\n");
            return;
        }

        System.out.println("✓ Modèle chargé: " + model.getName());
        System.out.println("✓ Nombre d'entités: " + model.getEntities().size());

        // Validation du modèle
        System.out.println("\n--- VALIDATION DU MODÈLE ---");
        List<String> errors = ModelValidator.validate(model);

        if (errors.isEmpty()) {
            System.out.println("✓ Aucune erreur détectée");

            // PrettyPrinter
            System.out.println("\n--- SYNTAXE MINISPEC ---");
            PrettyPrinter pp = new PrettyPrinter();
            model.accept(pp);
            System.out.println(pp.result());

            // JavaVisitor
            System.out.println("--- CODE JAVA GÉNÉRÉ ---");
            JavaVisitor javaVisitor = new JavaVisitor();
            model.accept(javaVisitor);
            System.out.println(javaVisitor.getResult());
        } else {
            System.out.println("✗ Erreurs détectées:");
            for (String error : errors) {
                System.out.println("  - " + error);
            }
            System.out.println("\n⚠ Génération de code annulée en raison des erreurs.\n");
        }
    }
}