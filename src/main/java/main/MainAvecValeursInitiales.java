package main;

import XMLIO.XMLAnalyser;
import codeGenConfig.CodeGenConfig;
import codeGenConfig.CodeGenConfigParser;
import metaModel.Model;
import metaModel.ModelValidator;
import visitor.JavaVisitor;
import prettyPrinter.PrettyPrinter;
import visitor.JavaVisitorWithConfig;

import java.util.List;

public class MainAvecValeursInitiales {
    public static void main(String[] args) throws Exception {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║            Génération de code avec Valeurs Initiales          ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        // Création de l'analyseur XML
        XMLAnalyser analyser = new XMLAnalyser();

        // Chargement du modèle depuis le fichier XML
        Model model = analyser.getModelFromFilenamed("src/main/resources/Exempleavecvaleursinitiales.xml");

        if (model == null) {
            System.out.println("✗ Erreur: impossible de charger le modèle");
            return;
        }

        System.out.println("✓ Modèle chargé: " + model.getName());
        System.out.println("✓ Nombre d'entités: " + model.getEntities().size());

        // Validation du modèle
        System.out.println("\n--- VALIDATION DU MODÈLE ---");
        List<String> errors = ModelValidator.validate(model);

        if (!errors.isEmpty()) {
            System.out.println("✗ Erreurs détectées:");
            for (String error : errors) {
                System.out.println("  - " + error);
            }
            System.out.println("\n⚠ Génération de code annulée en raison des erreurs.");
            return;
        }

        System.out.println("✓ Aucune erreur détectée");

        // PrettyPrinter
        System.out.println("\n" + "=".repeat(70));
        System.out.println("SYNTAXE MINISPEC AVEC VALEURS INITIALES");
        System.out.println("=".repeat(70) + "\n");

        PrettyPrinter pp = new PrettyPrinter();
        model.accept(pp);
        System.out.println(pp.result());

        // JavaVisitor
        System.out.println("=".repeat(70));
        System.out.println("CODE JAVA GÉNÉRÉ AVEC VALEURS INITIALES");
        System.out.println("=".repeat(70) + "\n");
        CodeGenConfigParser configParser = new CodeGenConfigParser();
        CodeGenConfig config = configParser.parseFromFile("src/main/resources/codeGenConfig.xml");
        JavaVisitorWithConfig javaVisitor = new JavaVisitorWithConfig(config);
        model.accept(javaVisitor);
        System.out.println(javaVisitor.getResult());

        // Résumé
        System.out.println("=".repeat(70));
        System.out.println("FONCTIONNALITÉS DÉMONTRÉES");
        System.out.println("=".repeat(70));
        System.out.println("✓ Valeurs primitives: false, 0.0");
        System.out.println("✓ Appels de constructeurs: new Point(0, 0)");
        System.out.println("✓ Appels de fonctions: nextId()");
        System.out.println("✓ Syntaxe minispec: := valeur");
        System.out.println("✓ Code Java: = valeur");
        System.out.println();
    }
}