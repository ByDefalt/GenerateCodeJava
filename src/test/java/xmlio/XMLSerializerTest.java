package xmlio;

import metaModel.minispec.*;
import metaModel.minispec.types.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import xmlio.metaModelCreator.XMLAnalyser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests complets pour XMLSerializer
 */
class XMLSerializerTest {

    private XMLSerializer serializer;
    private XMLAnalyser analyser;

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        serializer = new XMLSerializer();
        analyser = new XMLAnalyser();
    }

    /**
     * Test de base : modèle simple avec une entité
     */
    @Test
    void testSerializeSimpleModel() {
        // Créer un modèle simple
        Model model = new Model("TestModel");
        Entity entity = new Entity("Person", null, new ArrayList<>());
        entity.getAttributes().add(new Attribute("name", new SimpleType("String")));
        entity.getAttributes().add(new Attribute("age", new SimpleType("Integer")));
        model.getEntities().add(entity);

        // Sérialiser
        String xml = serializer.serializeToString(model);

        // Vérifier que le XML est généré
        assertNotNull(xml);
        assertTrue(xml.contains("<Model"));
        assertTrue(xml.contains("name=\"TestModel\""));
        assertTrue(xml.contains("<Entity"));
        assertTrue(xml.contains("name=\"Person\""));
        assertTrue(xml.contains("<Attribute"));
        assertTrue(xml.contains("name=\"name\""));
        assertTrue(xml.contains("name=\"age\""));
        assertTrue(xml.contains("type=\"String\""));
        assertTrue(xml.contains("type=\"Integer\""));
    }

    /**
     * Test : sérialisation et désérialisation (round-trip)
     */
    @Test
    void testRoundTrip() throws IOException {
        // Charger un modèle existant
        Model originalModel = (Model) analyser.getModelFromFilenamed("src/main/resources/Exemple3.xml");
        assertNotNull(originalModel);

        // Sérialiser dans un fichier temporaire
        File tempFile = new File(tempDir, "roundtrip.xml");
        boolean success = serializer.serializeToFile(originalModel, tempFile);
        assertTrue(success);

        // Désérialiser
        Model deserializedModel = (Model) analyser.getModelFromFile(tempFile);
        assertNotNull(deserializedModel);

        // Vérifier que les modèles sont équivalents
        assertEquals(originalModel.getName(), deserializedModel.getName());
        assertEquals(originalModel.getEntities().size(), deserializedModel.getEntities().size());

        // Vérifier la première entité
        if (!originalModel.getEntities().isEmpty()) {
            Entity originalEntity = originalModel.getEntities().get(0);
            Entity deserializedEntity = deserializedModel.getEntities().get(0);

            assertEquals(originalEntity.getName(), deserializedEntity.getName());
            assertEquals(originalEntity.getAttributes().size(), deserializedEntity.getAttributes().size());
        }
    }

    /**
     * Test : modèle avec héritage
     */
    @Test
    void testSerializeInheritance() {
        Model model = new Model("InheritanceModel");

        // Entité parent
        Entity parent = new Entity("Animal", null, new ArrayList<>());
        parent.getAttributes().add(new Attribute("name", new SimpleType("String")));
        model.getEntities().add(parent);

        // Entité enfant
        Entity child = new Entity("Dog", parent, new ArrayList<>());
        child.getAttributes().add(new Attribute("breed", new SimpleType("String")));
        model.getEntities().add(child);

        // Sérialiser
        String xml = serializer.serializeToString(model);

        // Vérifier
        assertNotNull(xml);
        assertTrue(xml.contains("name=\"Animal\""));
        assertTrue(xml.contains("name=\"Dog\""));
        assertTrue(xml.contains("extend="));
    }

    /**
     * Test : modèle avec collections
     */
    @Test
    void testSerializeCollections() {
        Model model = new Model("CollectionModel");

        Entity entity = new Entity("Team", null, new ArrayList<>());

        // Liste
        ListType listType = new ListType(new SimpleType("String"), 1, 10);
        entity.getAttributes().add(new Attribute("members", listType));

        // Array
        ArrayType arrayType = new ArrayType(new SimpleType("Integer"), 5);
        entity.getAttributes().add(new Attribute("scores", arrayType));

        // Set
        SetType setType = new SetType(new SimpleType("String"));
        entity.getAttributes().add(new Attribute("tags", setType));

        // Bag
        BagType bagType = new BagType(new SimpleType("String"));
        entity.getAttributes().add(new Attribute("items", bagType));

        model.getEntities().add(entity);

        // Sérialiser
        String xml = serializer.serializeToString(model);

        // Vérifier
        assertNotNull(xml);
        assertTrue(xml.contains("<List"));
        assertTrue(xml.contains("<Array"));
        assertTrue(xml.contains("<Set"));
        assertTrue(xml.contains("<Bag"));
    }

    /**
     * Test : modèle avec références
     */
    @Test
    void testSerializeReferences() {
        Model model = new Model("ReferenceModel");

        // Entité référencée
        Entity referencedEntity = new Entity("Company", null, new ArrayList<>());
        referencedEntity.getAttributes().add(new Attribute("name", new SimpleType("String")));
        model.getEntities().add(referencedEntity);

        // Entité avec référence
        Entity employee = new Entity("Employee", null, new ArrayList<>());
        employee.getAttributes().add(new Attribute("name", new SimpleType("String")));
        ResolvedReference reference = new ResolvedReference("Company", referencedEntity);
        employee.getAttributes().add(new Attribute("employer", reference));
        model.getEntities().add(employee);

        // Sérialiser
        String xml = serializer.serializeToString(model);

        // Vérifier
        assertNotNull(xml);
        assertTrue(xml.contains("<Reference"));
        assertTrue(xml.contains("name=\"Company\""));
    }

    /**
     * Test : valeurs initiales
     */
    @Test
    void testSerializeInitialValues() {
        Model model = new Model("InitModel");

        Entity entity = new Entity("Config", null, new ArrayList<>());
        entity.getAttributes().add(new Attribute("timeout", new SimpleType("Integer"), "30"));
        entity.getAttributes().add(new Attribute("enabled", new SimpleType("Boolean"), "true"));
        entity.getAttributes().add(new Attribute("method", new SimpleType("String"), "nextId()"));
        model.getEntities().add(entity);

        // Sérialiser
        String xml = serializer.serializeToString(model);

        // Vérifier
        assertNotNull(xml);
        assertTrue(xml.contains("init=\"30\""));
        assertTrue(xml.contains("init=\"true\""));
        assertTrue(xml.contains("init=\"nextId()\""));
    }

    /**
     * Test : sérialisation dans un fichier
     */
    @Test
    void testSerializeToFile() {
        Model model = new Model("FileModel");
        Entity entity = new Entity("TestEntity", null, new ArrayList<>());
        entity.getAttributes().add(new Attribute("field", new SimpleType("String")));
        model.getEntities().add(entity);

        File outputFile = new File(tempDir, "output.xml");
        boolean success = serializer.serializeToFile(model, outputFile);

        assertTrue(success);
        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
    }


    /**
     * Test : modèle vide
     */
    @Test
    void testSerializeEmptyModel() {
        Model model = new Model("EmptyModel");

        String xml = serializer.serializeToString(model);

        assertNotNull(xml);
        assertTrue(xml.contains("<Model"));
        assertTrue(xml.contains("name=\"EmptyModel\""));
    }

    /**
     * Test : entité sans attributs
     */
    @Test
    void testSerializeEntityWithoutAttributes() {
        Model model = new Model("TestModel");
        Entity entity = new Entity("EmptyEntity", null, new ArrayList<>());
        model.getEntities().add(entity);

        String xml = serializer.serializeToString(model);

        assertNotNull(xml);
        assertTrue(xml.contains("name=\"EmptyEntity\""));
    }

    /**
     * Test : hiérarchie d'héritage multiple
     */
    @Test
    void testSerializeMultiLevelInheritance() {
        Model model = new Model("HierarchyModel");

        // Grand-parent
        Entity grandParent = new Entity("Vehicle", null, new ArrayList<>());
        grandParent.getAttributes().add(new Attribute("wheels", new SimpleType("Integer")));
        model.getEntities().add(grandParent);

        // Parent
        Entity parent = new Entity("Car", grandParent, new ArrayList<>());
        parent.getAttributes().add(new Attribute("doors", new SimpleType("Integer")));
        model.getEntities().add(parent);

        // Enfant
        Entity child = new Entity("SportsCar", parent, new ArrayList<>());
        child.getAttributes().add(new Attribute("topSpeed", new SimpleType("Integer")));
        model.getEntities().add(child);

        // Sérialiser et désérialiser
        File tempFile = new File(tempDir, "hierarchy.xml");
        serializer.serializeToFile(model, tempFile);

        Model deserializedModel = (Model) analyser.getModelFromFile(tempFile);
        assertNotNull(deserializedModel);
        assertEquals(3, deserializedModel.getEntities().size());
    }

    /**
     * Test : collection de références
     */
    @Test
    void testSerializeCollectionOfReferences() {
        Model model = new Model("CollectionRefModel");

        // Entité référencée
        Entity student = new Entity("Student", null, new ArrayList<>());
        student.getAttributes().add(new Attribute("name", new SimpleType("String")));
        model.getEntities().add(student);

        // Entité avec collection de références
        Entity classroom = new Entity("Classroom", null, new ArrayList<>());
        ResolvedReference studentRef = new ResolvedReference("Student", student);
        ListType studentList = new ListType(studentRef, 0, null);
        classroom.getAttributes().add(new Attribute("students", studentList));
        model.getEntities().add(classroom);

        // Sérialiser
        String xml = serializer.serializeToString(model);

        // Vérifier
        assertNotNull(xml);
        assertTrue(xml.contains("<List"));
        assertTrue(xml.contains("<Reference"));

        // Round-trip
        File tempFile = new File(tempDir, "collection_ref.xml");
        serializer.serializeToFile(model, tempFile);

        Model deserializedModel = (Model) analyser.getModelFromFile(tempFile);
        assertNotNull(deserializedModel);
        assertEquals(2, deserializedModel.getEntities().size());
    }

    /**
     * Test : cardinalité des collections
     */
    @Test
    void testSerializeCollectionCardinality() {
        Model model = new Model("CardinalityModel");

        Entity entity = new Entity("Container", null, new ArrayList<>());

        // Liste avec min et max
        ListType listWithCardinality = new ListType(new SimpleType("String"), 1, 5);
        entity.getAttributes().add(new Attribute("limited", listWithCardinality));

        // Liste sans min/max
        ListType listUnbounded = new ListType(new SimpleType("String"));
        entity.getAttributes().add(new Attribute("unlimited", listUnbounded));

        model.getEntities().add(entity);

        // Sérialiser
        String xml = serializer.serializeToString(model);

        // Vérifier
        assertNotNull(xml);
        assertTrue(xml.contains("min=\"1\""));
        assertTrue(xml.contains("max=\"5\""));
    }

    /**
     * Test : nom de modèle null
     */
    @Test
    void testSerializeModelWithNullName() {
        Model model = new Model(null);
        Entity entity = new Entity("TestEntity", null, new ArrayList<>());
        model.getEntities().add(entity);

        String xml = serializer.serializeToString(model);

        assertNotNull(xml);
        assertTrue(xml.contains("<Model"));
    }
}
