package xmlio;

import metaModel.minispec.*;
import metaModel.minispec.types.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import xmlio.metaModelCreator.XMLAnalyser;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour XMLSerializer et XMLAnalyser
 * Vérifie que la sérialisation et désérialisation fonctionnent ensemble
 */
class XMLIntegrationTest {

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
     * Test : conservation de la hiérarchie d'héritage
     */
    @Test
    void testInheritancePreservation() {
        Model model = new Model("InheritanceModel");

        Entity vehicle = new Entity("Vehicle", null, new ArrayList<>());
        vehicle.getAttributes().add(new Attribute("wheels", new SimpleType("Integer")));
        model.getEntities().add(vehicle);

        Entity car = new Entity("Car", vehicle, new ArrayList<>());
        car.getAttributes().add(new Attribute("doors", new SimpleType("Integer")));
        model.getEntities().add(car);

        Entity sportsCar = new Entity("SportsCar", car, new ArrayList<>());
        sportsCar.getAttributes().add(new Attribute("topSpeed", new SimpleType("Integer")));
        model.getEntities().add(sportsCar);

        // Round-trip
        File tempFile = new File(tempDir, "inheritance.xml");
        serializer.serializeToFile(model, tempFile);
        Model loaded = (Model) analyser.getModelFromFile(tempFile);

        assertNotNull(loaded);
        assertEquals(3, loaded.getEntities().size());

        Entity loadedSportsCar = loaded.getEntities().stream()
                .filter(e -> "SportsCar".equals(e.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(loadedSportsCar);
        assertNotNull(loadedSportsCar.getSuperEntity());
        assertEquals("Car", loadedSportsCar.getSuperEntity().getName());
        assertNotNull(loadedSportsCar.getSuperEntity().getSuperEntity());
        assertEquals("Vehicle", loadedSportsCar.getSuperEntity().getSuperEntity().getName());
    }

    /**
     * Test : conservation des références
     */
    @Test
    void testReferencePreservation() {
        Model model = new Model("ReferenceModel");

        Entity company = new Entity("Company", null, new ArrayList<>());
        company.getAttributes().add(new Attribute("name", new SimpleType("String")));
        model.getEntities().add(company);

        Entity department = new Entity("Department", null, new ArrayList<>());
        department.getAttributes().add(new Attribute("name", new SimpleType("String")));
        ResolvedReference companyRef = new ResolvedReference("Company", company);
        department.getAttributes().add(new Attribute("parentCompany", companyRef));
        model.getEntities().add(department);

        Entity employee = new Entity("Employee", null, new ArrayList<>());
        employee.getAttributes().add(new Attribute("name", new SimpleType("String")));
        ResolvedReference deptRef = new ResolvedReference("Department", department);
        employee.getAttributes().add(new Attribute("department", deptRef));
        model.getEntities().add(employee);

        // Round-trip
        File tempFile = new File(tempDir, "references.xml");
        serializer.serializeToFile(model, tempFile);
        Model loaded = (Model) analyser.getModelFromFile(tempFile);

        assertNotNull(loaded);
        assertEquals(3, loaded.getEntities().size());

        Entity loadedEmployee = loaded.getEntities().stream()
                .filter(e -> "Employee".equals(e.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(loadedEmployee);
        Attribute deptAttr = loadedEmployee.getAttributes().stream()
                .filter(a -> "department".equals(a.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(deptAttr);
        assertTrue(deptAttr.getType() instanceof ResolvedReference);
        assertEquals("Department", ((ResolvedReference) deptAttr.getType()).getReferencedEntity().getName());
    }

    /**
     * Test : conservation des collections
     */
    @Test
    void testCollectionPreservation() {
        Model model = new Model("CollectionModel");

        Entity container = new Entity("Container", null, new ArrayList<>());
        
        container.getAttributes().add(new Attribute("list", 
                new ListType(new SimpleType("String"), 1, 10)));
        container.getAttributes().add(new Attribute("array", 
                new ArrayType(new SimpleType("Integer"), 5)));
        container.getAttributes().add(new Attribute("set", 
                new SetType(new SimpleType("String"))));
        container.getAttributes().add(new Attribute("bag", 
                new BagType(new SimpleType("String"), 0, 100)));
        
        model.getEntities().add(container);

        // Round-trip
        File tempFile = new File(tempDir, "collections.xml");
        serializer.serializeToFile(model, tempFile);
        Model loaded = (Model) analyser.getModelFromFile(tempFile);

        assertNotNull(loaded);
        Entity loadedContainer = loaded.getEntities().get(0);
        assertEquals(4, loadedContainer.getAttributes().size());

        // Vérifier list
        Attribute listAttr = loadedContainer.getAttributes().stream()
                .filter(a -> "list".equals(a.getName()))
                .findFirst()
                .orElse(null);
        assertTrue(listAttr.getType() instanceof ListType);
        ListType listType = (ListType) listAttr.getType();
        assertEquals(1, listType.getMinCardinality());
        assertEquals(10, listType.getMaxCardinality());

        // Vérifier array
        Attribute arrayAttr = loadedContainer.getAttributes().stream()
                .filter(a -> "array".equals(a.getName()))
                .findFirst()
                .orElse(null);
        assertTrue(arrayAttr.getType() instanceof ArrayType);
        assertEquals(5, ((ArrayType) arrayAttr.getType()).getSize());
    }

    /**
     * Test : conservation des valeurs initiales
     */
    @Test
    void testInitialValuePreservation() {
        Model model = new Model("InitValueModel");

        Entity config = new Entity("Config", null, new ArrayList<>());
        config.getAttributes().add(new Attribute("timeout", new SimpleType("Integer"), "30"));
        config.getAttributes().add(new Attribute("enabled", new SimpleType("Boolean"), "true"));
        config.getAttributes().add(new Attribute("name", new SimpleType("String"), "default"));
        config.getAttributes().add(new Attribute("id", new SimpleType("Integer"), "nextId()"));
        model.getEntities().add(config);

        // Round-trip
        File tempFile = new File(tempDir, "init_values.xml");
        serializer.serializeToFile(model, tempFile);
        Model loaded = (Model) analyser.getModelFromFile(tempFile);

        assertNotNull(loaded);
        Entity loadedConfig = loaded.getEntities().get(0);

        Attribute timeoutAttr = loadedConfig.getAttributes().stream()
                .filter(a -> "timeout".equals(a.getName()))
                .findFirst()
                .orElse(null);
        assertEquals("30", timeoutAttr.getInitialValue());

        Attribute idAttr = loadedConfig.getAttributes().stream()
                .filter(a -> "id".equals(a.getName()))
                .findFirst()
                .orElse(null);
        assertEquals("nextId()", idAttr.getInitialValue());
    }

    /**
     * Test : fichiers d'exemple réels
     */
    @Test
    void testRealExampleFiles() {
        // Exemple 3
        Model exemple3 = (Model) analyser.getModelFromFilenamed("src/main/resources/Exemple3.xml");
        assertNotNull(exemple3);
        
        File temp3 = new File(tempDir, "exemple3_copy.xml");
        serializer.serializeToFile(exemple3, temp3);
        
        Model loaded3 = (Model) analyser.getModelFromFile(temp3);
        assertNotNull(loaded3);
        assertEquals(exemple3.getName(), loaded3.getName());
        assertEquals(exemple3.getEntities().size(), loaded3.getEntities().size());

        // Exemple avec collections
        Model exampleColl = (Model) analyser.getModelFromFilenamed("src/main/resources/exempleWithCollections.xml");
        assertNotNull(exampleColl);
        
        File tempColl = new File(tempDir, "collections_copy.xml");
        serializer.serializeToFile(exampleColl, tempColl);
        
        Model loadedColl = (Model) analyser.getModelFromFile(tempColl);
        assertNotNull(loadedColl);
        assertEquals(exampleColl.getName(), loadedColl.getName());
        assertEquals(exampleColl.getEntities().size(), loadedColl.getEntities().size());
    }

    /**
     * Test : multiples round-trips successifs
     */
    @Test
    void testMultipleRoundTrips() {
        Model original = createCompleteModel();

        Model current = original;
        for (int i = 0; i < 3; i++) {
            File tempFile = new File(tempDir, "roundtrip_" + i + ".xml");
            serializer.serializeToFile(current, tempFile);
            current = (Model) analyser.getModelFromFile(tempFile);
            assertNotNull(current);
        }

        assertEquals(original.getName(), current.getName());
        assertEquals(original.getEntities().size(), current.getEntities().size());
    }

    /**
     * Test : grand modèle avec beaucoup d'entités
     */
    @Test
    void testLargeModel() {
        Model model = new Model("LargeModel");

        // Créer 50 entités
        for (int i = 0; i < 50; i++) {
            Entity entity = new Entity("Entity" + i, null, new ArrayList<>());
            
            // Ajouter des attributs
            for (int j = 0; j < 5; j++) {
                entity.getAttributes().add(
                    new Attribute("field" + j, new SimpleType("String"))
                );
            }
            
            model.getEntities().add(entity);
        }

        // Round-trip
        File tempFile = new File(tempDir, "large_model.xml");
        serializer.serializeToFile(model, tempFile);
        Model loaded = (Model) analyser.getModelFromFile(tempFile);

        assertNotNull(loaded);
        assertEquals(50, loaded.getEntities().size());
        
        for (Entity entity : loaded.getEntities()) {
            assertEquals(5, entity.getAttributes().size());
        }
    }

    /**
     * Test : modèle avec relations bidirectionnelles
     */
    @Test
    void testBidirectionalRelations() {
        Model model = new Model("BidirectionalModel");

        Entity person = new Entity("Person", null, new ArrayList<>());
        person.getAttributes().add(new Attribute("name", new SimpleType("String")));
        model.getEntities().add(person);

        Entity address = new Entity("Address", null, new ArrayList<>());
        address.getAttributes().add(new Attribute("street", new SimpleType("String")));
        
        // Person -> Address
        ResolvedReference addressRef = new ResolvedReference("Address", address);
        person.getAttributes().add(new Attribute("address", addressRef));
        
        // Address -> Person
        ResolvedReference personRef = new ResolvedReference("Person", person);
        address.getAttributes().add(new Attribute("resident", personRef));
        
        model.getEntities().add(address);

        // Round-trip
        File tempFile = new File(tempDir, "bidirectional.xml");
        serializer.serializeToFile(model, tempFile);
        Model loaded = (Model) analyser.getModelFromFile(tempFile);

        assertNotNull(loaded);
        assertEquals(2, loaded.getEntities().size());
    }

    /**
     * Méthode utilitaire pour créer un modèle complet
     */
    private Model createCompleteModel() {
        Model model = new Model("CompleteModel");

        // Entité de base
        Entity baseEntity = new Entity("BaseEntity", null, new ArrayList<>());
        baseEntity.getAttributes().add(new Attribute("id", new SimpleType("Integer")));
        baseEntity.getAttributes().add(new Attribute("name", new SimpleType("String")));
        model.getEntities().add(baseEntity);

        // Entité dérivée
        Entity derivedEntity = new Entity("DerivedEntity", baseEntity, new ArrayList<>());
        derivedEntity.getAttributes().add(new Attribute("description", new SimpleType("String")));
        
        // Avec collection
        ListType stringList = new ListType(new SimpleType("String"), 0, 10);
        derivedEntity.getAttributes().add(new Attribute("tags", stringList));
        
        // Avec référence
        ResolvedReference baseRef = new ResolvedReference("BaseEntity", baseEntity);
        derivedEntity.getAttributes().add(new Attribute("parent", baseRef));
        
        // Avec valeur initiale
        derivedEntity.getAttributes().add(new Attribute("counter", new SimpleType("Integer"), "0"));
        
        model.getEntities().add(derivedEntity);

        return model;
    }
}
