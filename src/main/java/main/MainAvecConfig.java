package main;

import XMLIO.XMLAnalyser;
import codeGenConfig.CodeGenConfig;
import codeGenConfig.CodeGenConfigParser;
import codeGenConfig.DependencyResolver;
import metaModel.Model;
import metaModel.ModelValidator;
import visitor.JavaVisitorWithConfig;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainAvecConfig {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║     Génération de code avec Configuration et Dépendances      ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        try {
            // 1. Charger la configuration
            System.out.println("--- CHARGEMENT DE LA CONFIGURATION ---");
            CodeGenConfigParser configParser = new CodeGenConfigParser();
            CodeGenConfig config = configParser.parseFromFile("src/main/resources/codeGenConfig.xml");
            System.out.println("✓ Configuration chargée");
            System.out.println("  - Modèles configurés: " + config.getModelMappings().size());
            System.out.println("  - Types primitifs configurés: " + config.getPrimitiveMappings().size());

            // 2. Charger le modèle minispec
            System.out.println("\n--- CHARGEMENT DU MODÈLE MINISPEC ---");
            XMLAnalyser analyser = new XMLAnalyser();
            Model model = analyser.getModelFromFilenamed("src/main/resources/ExempleAvecHeritage.xml");

            if (model == null) {
                System.out.println("✗ Erreur: impossible de charger le modèle");
                return;
            }

            System.out.println("✓ Modèle chargé: " + model.getName());
            System.out.println("✓ Nombre d'entités: " + model.getEntities().size());

            // 3. Valider le modèle
            System.out.println("\n--- VALIDATION DU MODÈLE ---");
            List<String> errors = ModelValidator.validate(model);

            if (!errors.isEmpty()) {
                System.out.println("✗ Erreurs détectées:");
                for (String error : errors) {
                    System.out.println("  - " + error);
                }
                System.out.println("\n⚠ Génération de code annulée.");
                return;
            }

            System.out.println("✓ Aucune erreur détectée");

            // 4. Analyser les dépendances
            System.out.println("\n--- ANALYSE DES DÉPENDANCES ---");
            DependencyResolver resolver = new DependencyResolver(config);
            Map<String, Set<String>> dependencies = resolver.buildDependencyGraph(model);

            System.out.println("Graphe de dépendances:");
            for (Map.Entry<String, Set<String>> entry : dependencies.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    System.out.println("  " + entry.getKey() + " dépend de: " + entry.getValue());
                }
            }

            // Vérifier les cycles de dépendances
            List<String> cycles = resolver.detectCyclicDependencies(dependencies);
            if (!cycles.isEmpty()) {
                System.out.println("\n⚠ Cycles de dépendances détectés:");
                for (String cycle : cycles) {
                    System.out.println("  - " + cycle);
                }
            }

            // Calculer l'ordre topologique
            List<String> topOrder = resolver.topologicalSort(dependencies);
            System.out.println("\nOrdre topologique de génération:");
            System.out.println("  " + String.join(" -> ", topOrder));

            // 5. Générer le code Java
            System.out.println("\n" + "=".repeat(70));
            System.out.println("CODE JAVA GÉNÉRÉ AVEC PACKAGES ET IMPORTS");
            System.out.println("=".repeat(70) + "\n");

            JavaVisitorWithConfig javaVisitor = new JavaVisitorWithConfig(config);
            model.accept(javaVisitor);
            System.out.println(javaVisitor.getResult());

            // 6. Afficher le résumé
            System.out.println("=".repeat(70));
            System.out.println("RÉSUMÉ");
            System.out.println("=".repeat(70));
            System.out.println("✓ Package Java: " + config.getPackageForModel(model.getName()));
            System.out.println("✓ Entités générées: " + model.getEntities().size());
            System.out.println("✓ Imports automatiques ajoutés");
            System.out.println("✓ Dépendances résolues");
            System.out.println("✓ Code prêt à compiler!");

        } catch (Exception e) {
            System.err.println("✗ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}