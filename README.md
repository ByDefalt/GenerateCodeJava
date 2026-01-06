# ProcGen - Générateur de Code Procédural

## Description

ProcGen est un outil de génération de code basé sur un métamodèle. Il permet de transformer des modèles XML (format MiniSpec) en code Java en utilisant le pattern Visitor et un système de délégation.

## Prérequis

- **Java JDK 22** ou supérieur
- **Gradle 8.8** (inclus via Gradle Wrapper)
- Un IDE compatible Java (IntelliJ IDEA, Eclipse, VS Code recommandés)

## Structure du Projet

```
ProcGen/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── main/               # Point d'entrée de l'application
│   │   │   ├── metaModel/          # Définition du métamodèle
│   │   │   ├── visitor/            # Pattern Visitor et délégateurs
│   │   │   ├── prettyPrinter/      # Affichage formaté du modèle
│   │   │   └── xmlio/              # Sérialisation/Désérialisation XML
│   │   └── resources/
│   │       ├── Exemple3.xml        # Exemple simple
│   │       ├── exempleWithCollections.xml  # Exemple avec collections
│   │       └── javaConfig.xml      # Configuration Java
│   └── test/
│       └── java/                   # Tests unitaires et d'intégration
├── build.gradle.kts                # Configuration Gradle
└── README.md                       # Ce fichier
```

## Installation

### Option 1 : Ligne de commande (Recommandé)

1. **Cloner ou extraire le projet**
   ```bash
   cd ProcGen
   ```

2. **Vérifier l'installation Java**
   ```bash
   java -version
   ```
   Assurez-vous d'avoir Java 22 ou supérieur.

3. **Compiler le projet**
   ```bash
   # Sur Linux/Mac
   ./gradlew build
   
   # Sur Windows
   gradlew.bat build
   ```

### Option 2 : Avec un IDE

#### IntelliJ IDEA
1. Ouvrir IntelliJ IDEA
2. File → Open → Sélectionner le dossier `ProcGen`
3. Attendre que Gradle synchronise le projet
4. Le projet devrait se compiler automatiquement

#### Eclipse
1. Ouvrir Eclipse
2. File → Import → Gradle → Existing Gradle Project
3. Sélectionner le dossier `ProcGen`
4. Terminer l'import

#### VS Code
1. Ouvrir VS Code
2. Installer l'extension "Extension Pack for Java"
3. Ouvrir le dossier `ProcGen`
4. Le projet devrait se configurer automatiquement

## Exécution

### Méthode 1 : Exécuter la classe Main

#### Ligne de commande
```bash
# Sur Linux/Mac
./gradlew run

# Sur Windows
gradlew.bat run
```

#### Depuis un IDE
1. Ouvrir le fichier `src/main/java/main/Main.java`
2. Clic droit → Run 'Main.main()'

### Méthode 2 : Exécuter les tests

Pour vérifier que tout fonctionne correctement :

```bash
# Exécuter tous les tests
./gradlew test

# Exécuter un test spécifique
./gradlew test --tests prettyPrinter.PrettyPrinterTest
./gradlew test --tests xmlio.XMLAnalyserComprehensiveTest
```

## Utilisation

### Génération de Code Java

Le programme principal (`Main.java`) effectue les opérations suivantes :

1. **Charge un modèle XML** depuis `src/main/resources/exempleWithCollections.xml`
2. **Charge une configuration Java** depuis `src/main/resources/javaConfig.xml`
3. **Génère du code Java** en utilisant le JavaVisitor
4. **Affiche le résultat** dans la console

### Exemple de sortie

Le programme génère des classes Java avec :
- Déclarations de package
- Imports automatiques
- Champs privés
- Getters et setters
- Méthodes pour manipuler les collections

### Fichiers d'exemple fournis

1. **Exemple3.xml** : Modèle simple avec une entité Satellite
   ```xml
   <Model name="Exemple3">
     <Entity name="Satellite">
       <Attribute name="nom" type="String"/>
       <Attribute name="id" type="Integer"/>
     </Entity>
   </Model>
   ```

2. **exempleWithCollections.xml** : Modèle avec héritage et collections
   - Entité Flotte héritant de Satellite
   - Liste de satellites
   - Références entre entités

## Fonctionnalités Principales

### 1. Chargement de Modèles XML
```java
XMLAnalyser analyser = new XMLAnalyser();
Model model = (Model) analyser.getModelFromFilenamed("chemin/vers/modele.xml");
```

### 2. Configuration de la Génération
```java
JavaMetaModelConfiguration config = (JavaMetaModelConfiguration) 
    analyser.getModelFromFilenamed("chemin/vers/config.xml");
```

### 3. Génération de Code
```java
CodeGenVisitor javaVisitor = new JavaVisitor();
javaVisitor.setMetaModelConfiguration(config);
model.accept(javaVisitor);
String generatedCode = javaVisitor.getResult();
```

### 4. Pretty Printing
```java
PrettyPrinter prettyPrinter = new PrettyPrinter();
model.accept(prettyPrinter);
System.out.println(prettyPrinter.result());
```

## Architecture

### Pattern Visitor
Le projet utilise le pattern Visitor avec un système de délégation :

- **Visitor** : Interface de base pour parcourir le métamodèle
- **CodeGenVisitor** : Visitor générique pour la génération de code
- **JavaVisitor** : Implémentation spécifique pour Java
- **Delegators** : Gèrent la génération pour chaque type d'élément
  - ModelJavaCodeGenDelegator
  - EntityJavaCodeGenDelegator
  - AttributeJavaCodeGenDelegator
  - CollectionJavaCodeGenDelegator

### Métamodèle
Le métamodèle supporte :
- **Entités** avec héritage
- **Attributs** avec types primitifs ou références
- **Collections** : List, Set, Bag, Array
- **Références** : ResolvedReference, UnresolvedReference
- **Valeurs initiales** pour les attributs

## Tests

Le projet inclut des tests complets :

- **PrettyPrinterTest** : Test de l'affichage formaté
- **XMLAnalyserComprehensiveTest** : Tests de chargement XML
- **XMLSerializerTest** : Tests de sérialisation
- **XMLIntegrationTest** : Tests d'intégration complets

Pour exécuter tous les tests :
```bash
./gradlew test
```

Pour voir le rapport de tests détaillé :
```bash
./gradlew test --info
```

## Dépannage

### Erreur : "Java version incompatible"
- Vérifiez que Java 22+ est installé : `java -version`
- Définissez JAVA_HOME vers votre JDK 22

### Erreur : "Permission denied" (Linux/Mac)
```bash
chmod +x gradlew
```

### Erreur : Build échoué
```bash
# Nettoyer et rebuild
./gradlew clean build
```

### IDE ne trouve pas les dépendances
```bash
# Rafraîchir les dépendances Gradle
./gradlew --refresh-dependencies
```

## Contact et Support

Pour toute question concernant le projet, veuillez consulter :
- La documentation dans le code source
- Les tests unitaires comme exemples d'utilisation
- Le fichier `jsp.txt` pour les notes de conception

## Licence

Projet académique - M2 TIIL

---

**Note pour l'évaluation** : 
Le programme principal se lance simplement avec `./gradlew run` ou en exécutant `Main.java` depuis un IDE. 
Les résultats de génération s'affichent dans la console.
