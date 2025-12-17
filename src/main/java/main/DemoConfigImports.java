package main;

import metaModel.Model;
import visitor.CodeGenVisitor;
import visitor.config.TypeMappingConfig;
import visitor.imports.ImportCollectorRegistry;
import visitor.java.JavaImportsVisitor;
import visitor.java.JavaVisitor;
import xmlio.metaModelCreator.XMLAnalyser;

/**
 * Démonstration complète du système de configuration des imports.
 * 
 * Ce programme montre:
 * 1. Chargement de la configuration depuis XML
 * 2. Génération de code avec imports automatiques
 * 3. Comparaison avant/après la configuration
 */
public class DemoConfigImports {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  DÉMONSTRATION : Système de Configuration des Imports        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // ===== ÉTAPE 1: Charger le modèle =====
        System.out.println("📄 ÉTAPE 1: Chargement du modèle de test...");
        System.out.println("   Fichier: testImportsConfig.xml");
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromFilenamed("src/main/resources/testImportsConfig.xml");
        
        if (model == null) {
            System.err.println("❌ Erreur: impossible de charger le modèle");
            return;
        }
        System.out.println("✅ Modèle chargé avec succès: " + model.getName());
        System.out.println("   Nombre d'entités: " + model.getEntities().size());
        System.out.println();
        
        // ===== ÉTAPE 2: Charger la configuration =====
        System.out.println("⚙️  ÉTAPE 2: Chargement de la configuration des types...");
        System.out.println("   Fichier: type-mapping-config.xml");
        TypeMappingConfig config = new TypeMappingConfig();
        config.loadFromXml("src/main/resources/type-mapping-config.xml");
        System.out.println("✅ Configuration chargée");
        System.out.println();
        
        System.out.println("📋 Configuration des mappings:");
        System.out.println("   ─────────────────────────────────────────────");
        config.printConfiguration();
        System.out.println("   ─────────────────────────────────────────────");
        System.out.println();
        
        // ===== ÉTAPE 3: Générer le code SANS imports =====
        System.out.println("🔧 ÉTAPE 3: Génération du code SANS gestion des imports...");
        CodeGenVisitor basicVisitor = new JavaVisitor();
        model.accept(basicVisitor);
        String codeWithoutImports = basicVisitor.getResult();
        
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ CODE GÉNÉRÉ (sans imports)                                  │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println(codeWithoutImports);
        System.out.println();
        
        // ===== ÉTAPE 4: Générer le code AVEC imports =====
        System.out.println("✨ ÉTAPE 4: Génération du code AVEC imports automatiques...");
        ImportCollectorRegistry registry = new ImportCollectorRegistry(config);
        JavaImportsVisitor importsVisitor = new JavaImportsVisitor(new JavaVisitor(), registry);
        model.accept(importsVisitor);
        String codeWithImports = importsVisitor.getResult();
        
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ CODE GÉNÉRÉ (avec imports)                                  │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println(codeWithImports);
        System.out.println();
        
        // ===== ÉTAPE 5: Analyse des différences =====
        System.out.println("📊 ÉTAPE 5: Analyse des imports ajoutés...");
        analyzeImports(codeWithoutImports, codeWithImports);
        System.out.println();
        
        // ===== CONCLUSION =====
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  AVANTAGES DU SYSTÈME DE CONFIGURATION                       ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        System.out.println("║  ✅ Pas de code Java à modifier pour ajouter des types       ║");
        System.out.println("║  ✅ Configuration externalisée dans XML                      ║");
        System.out.println("║  ✅ Respecte le principe Open/Closed (OCP)                   ║");
        System.out.println("║  ✅ Facile à maintenir et à étendre                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        System.out.println("💡 Pour ajouter un nouveau type:");
        System.out.println("   1. Ouvrez type-mapping-config.xml");
        System.out.println("   2. Ajoutez: <primitive name=\"MonType\" type=\"MonType\" package=\"com.example.MonType\"/>");
        System.out.println("   3. C'est tout! Pas de code Java à modifier.");
    }
    
    private static void analyzeImports(String codeWithout, String codeWith) {
        String[] linesWithout = codeWithout.split("\n");
        String[] linesWith = codeWith.split("\n");
        
        int importsCount = 0;
        System.out.println("   Imports détectés et ajoutés automatiquement:");
        
        for (String line : linesWith) {
            if (line.trim().startsWith("import ")) {
                importsCount++;
                System.out.println("   • " + line.trim());
            }
        }
        
        if (importsCount == 0) {
            System.out.println("   (aucun import nécessaire pour ce modèle)");
        } else {
            System.out.println();
            System.out.println("   Total: " + importsCount + " import(s) ajouté(s) automatiquement");
        }
    }
}
