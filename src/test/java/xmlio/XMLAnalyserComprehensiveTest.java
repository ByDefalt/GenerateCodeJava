package xmlio;

import metaModel.minispec.*;
import metaModel.minispec.types.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import xmlio.metaModelCreator.XMLAnalyser;

import java.io.ByteArrayInputStream;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests complets pour XMLAnalyser
 */
class XMLAnalyserComprehensiveTest {

    private XMLAnalyser analyser;

    @BeforeEach
    void setUp() {
        analyser = new XMLAnalyser();
    }

    /**
     * Test de base : chargement depuis fichier
     */
    @Test
    void testLoadFromFile() {
        Model model = (Model) analyser.getModelFromFilenamed("src/main/resources/Exemple3.xml");
        
        assertNotNull(model, "Le modèle ne devrait pas être null");
        assertEquals("Exemple3", model.getName(), "Le nom du modèle devrait être 'Exemple3'");
        assertEquals(1, model.getEntities().size(), "Le modèle devrait avoir 1 entité");
        
        Entity entity = model.getEntities().get(0);
        assertEquals("Satellite", entity.getName(), "L'entité devrait s'appeler 'Satellite'");
        assertEquals(2, entity.getAttributes().size(), "L'entité devrait avoir 2 attributs");
    }

    /**
     * Test : chargement du modèle avec collections
     */
    @Test
    void testLoadModelWithCollections() {
        Model model = (Model) analyser.getModelFromFilenamed("src/main/resources/exempleWithCollections.xml");
        
        assertNotNull(model);
        assertEquals("SpaceMissionHybrid", model.getName());
        assertEquals(2, model.getEntities().size());
        
        // Vérifier l'entité Flotte
        Entity flotte = model.getEntities().stream()
                .filter(e -> "Flotte".equals(e.getName()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(flotte, "L'entité Flotte devrait exister");
        assertNotNull(flotte.getSuperEntity(), "Flotte devrait hériter de Satellite");
        assertEquals("Satellite", flotte.getSuperEntity().getName());
        
        // Vérifier les attributs de Flotte
        assertTrue(flotte.getAttributes().size() >= 1);
        Attribute nomAttr = flotte.getAttributes().stream()
                .filter(a -> "nom".equals(a.getName()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(nomAttr);
        assertTrue(nomAttr.getType() instanceof SimpleType);
        assertEquals("String", ((SimpleType) nomAttr.getType()).getTypeName());
    }

    /**
     * Test : chargement depuis String
     */
    @Test
    void testLoadFromString() {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Root model="#1">
                    <Model id="#1" name="TestModel"/>
                    <Entity id="#2" model="#1" name="Person" />
                    <Attribute id="#3" entity="#2" name="name" type="String" />
                </Root>
                """;
        
        Model model = (Model) analyser.getModelFromString(xmlContent);
        
        assertNotNull(model);
        assertEquals("TestModel", model.getName());
        assertEquals(1, model.getEntities().size());
        assertEquals("Person", model.getEntities().get(0).getName());
    }

    /**
     * Test : chargement depuis InputStream
     */
    @Test
    void testLoadFromInputStream() {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Root model="#1">
                    <Model id="#1" name="StreamModel"/>
                    <Entity id="#2" model="#1" name="TestEntity" />
                </Root>
                """;
        
        ByteArrayInputStream stream = new ByteArrayInputStream(xmlContent.getBytes());
        Model model = (Model) analyser.getModelFromInputStream(stream);
        
        assertNotNull(model);
        assertEquals("StreamModel", model.getName());
    }

    /**
     * Test : héritage simple
     */
    @Test
    void testSimpleInheritance() {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Root model="#1">
                    <Model id="#1" name="InheritanceTest"/>
                    <Entity id="#2" model="#1" name="Animal" />
                    <Attribute id="#3" entity="#2" name="name" type="String" />
                    <Entity id="#4" model="#1" name="Dog" extend="#2"/>
                    <Attribute id="#5" entity="#4" name="breed" type="String" />
                </Root>
                """;
        
        Model model = (Model) analyser.getModelFromString(xmlContent);
        
        assertNotNull(model);
        assertEquals(2, model.getEntities().size());
        
        Entity dog = model.getEntities().stream()
                .filter(e -> "Dog".equals(e.getName()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(dog);
        assertNotNull(dog.getSuperEntity());
        assertEquals("Animal", dog.getSuperEntity().getName());
    }

    /**
     * Test : références résolues
     */
    @Test
    void testResolvedReferences() {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Root model="#1">
                    <Model id="#1" name="RefTest"/>
                    <Entity id="#2" model="#1" name="Company" />
                    <Attribute id="#3" entity="#2" name="name" type="String" />
                    <Entity id="#4" model="#1" name="Employee" />
                    <Reference id="#5" name="Company" entity="#2" />
                    <Attribute id="#6" entity="#4" name="employer" type="#5" />
                </Root>
                """;
        
        Model model = (Model) analyser.getModelFromString(xmlContent);
        
        assertNotNull(model);
        
        Entity employee = model.getEntities().stream()
                .filter(e -> "Employee".equals(e.getName()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(employee);
        Attribute employerAttr = employee.getAttributes().stream()
                .filter(a -> "employer".equals(a.getName()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(employerAttr);
        assertTrue(employerAttr.getType() instanceof ResolvedReference);
        
        ResolvedReference ref = (ResolvedReference) employerAttr.getType();
        assertEquals("Company", ref.getReferencedEntity().getName());
    }

    /**
     * Test : types de collection Array
     */
    @Test
    void testArrayType() {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Root model="#1">
                    <Model id="#1" name="ArrayTest"/>
                    <Entity id="#2" model="#1" name="Container" />
                    <Array id="#3" of="String" size="5" />
                    <Attribute id="#4" entity="#2" name="items" type="#3" />
                </Root>
                """;
        
        Model model = (Model) analyser.getModelFromString(xmlContent);
        
        assertNotNull(model);
        Entity container = model.getEntities().get(0);
        Attribute itemsAttr = container.getAttributes().get(0);
        
        assertTrue(itemsAttr.getType() instanceof ArrayType);
        ArrayType arrayType = (ArrayType) itemsAttr.getType();
        assertEquals(5, arrayType.getSize());
        assertTrue(arrayType.getElementType() instanceof SimpleType);
    }

    /**
     * Test : types de collection List
     */
    @Test
    void testListType() {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Root model="#1">
                    <Model id="#1" name="ListTest"/>
                    <Entity id="#2" model="#1" name="Container" />
                    <List id="#3" of="Integer" min="1" max="10" />
                    <Attribute id="#4" entity="#2" name="numbers" type="#3" />
                </Root>
                """;
        
        Model model = (Model) analyser.getModelFromString(xmlContent);
        
        assertNotNull(model);
        Entity container = model.getEntities().get(0);
        Attribute numbersAttr = container.getAttributes().get(0);
        
        assertTrue(numbersAttr.getType() instanceof ListType);
        ListType listType = (ListType) numbersAttr.getType();
        assertEquals(1, listType.getMinCardinality());
        assertEquals(10, listType.getMaxCardinality());
    }

    /**
     * Test : types de collection Set
     */
    @Test
    void testSetType() {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Root model="#1">
                    <Model id="#1" name="SetTest"/>
                    <Entity id="#2" model="#1" name="Container" />
                    <Set id="#3" of="String" />
                    <Attribute id="#4" entity="#2" name="tags" type="#3" />
                </Root>
                """;
        
        Model model = (Model) analyser.getModelFromString(xmlContent);
        
        assertNotNull(model);
        Entity container = model.getEntities().get(0);
        Attribute tagsAttr = container.getAttributes().get(0);
        
        assertTrue(tagsAttr.getType() instanceof SetType);
    }

    /**
     * Test : types de collection Bag
     */
    @Test
    void testBagType() {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Root model="#1">
                    <Model id="#1" name="BagTest"/>
                    <Entity id="#2" model="#1" name="Container" />
                    <Bag id="#3" of="String" min="0" max="100" />
                    <Attribute id="#4" entity="#2" name="items" type="#3" />
                </Root>
                """;
        
        Model model = (Model) analyser.getModelFromString(xmlContent);
        
        assertNotNull(model);
        Entity container = model.getEntities().get(0);
        Attribute itemsAttr = container.getAttributes().get(0);
        
        assertTrue(itemsAttr.getType() instanceof BagType);
        BagType bagType = (BagType) itemsAttr.getType();
        assertEquals(0, bagType.getMinCardinality());
        assertEquals(100, bagType.getMaxCardinality());
    }

    /**
     * Test : valeurs initiales
     */
    @Test
    void testInitialValues() {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Root model="#1">
                    <Model id="#1" name="InitTest"/>
                    <Entity id="#2" model="#1" name="Config" />
                    <Attribute id="#3" entity="#2" name="timeout" type="Integer" init="30" />
                    <Attribute id="#4" entity="#2" name="enabled" type="Boolean" init="true" />
                </Root>
                """;
        
        Model model = (Model) analyser.getModelFromString(xmlContent);
        
        assertNotNull(model);
        Entity config = model.getEntities().get(0);
        
        Attribute timeoutAttr = config.getAttributes().stream()
                .filter(a -> "timeout".equals(a.getName()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(timeoutAttr);
        assertEquals("30", timeoutAttr.getInitialValue());
        
        Attribute enabledAttr = config.getAttributes().stream()
                .filter(a -> "enabled".equals(a.getName()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(enabledAttr);
        assertEquals("true", enabledAttr.getInitialValue());
    }

    /**
     * Test : fichier inexistant
     */
    @Test
    void testNonExistentFile() {
        Model model = (Model) analyser.getModelFromFilenamed("non_existent_file.xml");
        assertNull(model, "Le modèle devrait être null pour un fichier inexistant");
    }

    /**
     * Test : XML invalide
     */
    @Test
    void testInvalidXML() {
        String invalidXml = "<Root><Invalid>";
        Model model = (Model) analyser.getModelFromString(invalidXml);
        assertNull(model, "Le modèle devrait être null pour un XML invalide");
    }

    /**
     * Test : détection de cycle (héritage circulaire)
     */
    @Test
    void testCircularInheritance() {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Root model="#1">
                    <Model id="#1" name="CycleTest"/>
                    <Entity id="#2" model="#1" name="A" extend="#3"/>
                    <Entity id="#3" model="#1" name="B" extend="#2"/>
                </Root>
                """;
        
        // Le système devrait détecter le cycle et gérer l'erreur
        Model model = (Model) analyser.getModelFromString(xmlContent);
        // Le modèle peut être créé mais les entités auront des erreurs
        assertNotNull(model);
    }

    /**
     * Test : entités multiples
     */
    @Test
    void testMultipleEntities() {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Root model="#1">
                    <Model id="#1" name="MultiEntityTest"/>
                    <Entity id="#2" model="#1" name="Entity1" />
                    <Entity id="#3" model="#1" name="Entity2" />
                    <Entity id="#4" model="#1" name="Entity3" />
                    <Entity id="#5" model="#1" name="Entity4" />
                    <Entity id="#6" model="#1" name="Entity5" />
                </Root>
                """;
        
        Model model = (Model) analyser.getModelFromString(xmlContent);
        
        assertNotNull(model);
        assertEquals(5, model.getEntities().size());
    }

    /**
     * Test : attributs multiples
     */
    @Test
    void testMultipleAttributes() {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Root model="#1">
                    <Model id="#1" name="MultiAttrTest"/>
                    <Entity id="#2" model="#1" name="Person" />
                    <Attribute id="#3" entity="#2" name="firstName" type="String" />
                    <Attribute id="#4" entity="#2" name="lastName" type="String" />
                    <Attribute id="#5" entity="#2" name="age" type="Integer" />
                    <Attribute id="#6" entity="#2" name="email" type="String" />
                    <Attribute id="#7" entity="#2" name="phone" type="String" />
                </Root>
                """;
        
        Model model = (Model) analyser.getModelFromString(xmlContent);
        
        assertNotNull(model);
        Entity person = model.getEntities().get(0);
        assertEquals(5, person.getAttributes().size());
    }

    /**
     * Test : hiérarchie d'héritage à plusieurs niveaux
     */
    @Test
    void testMultiLevelInheritance() {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Root model="#1">
                    <Model id="#1" name="HierarchyTest"/>
                    <Entity id="#2" model="#1" name="GrandParent" />
                    <Attribute id="#3" entity="#2" name="level1" type="String" />
                    <Entity id="#4" model="#1" name="Parent" extend="#2"/>
                    <Attribute id="#5" entity="#4" name="level2" type="String" />
                    <Entity id="#6" model="#1" name="Child" extend="#4"/>
                    <Attribute id="#7" entity="#6" name="level3" type="String" />
                </Root>
                """;
        
        Model model = (Model) analyser.getModelFromString(xmlContent);
        
        assertNotNull(model);
        assertEquals(3, model.getEntities().size());
        
        Entity child = model.getEntities().stream()
                .filter(e -> "Child".equals(e.getName()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(child);
        assertNotNull(child.getSuperEntity());
        assertEquals("Parent", child.getSuperEntity().getName());
        assertNotNull(child.getSuperEntity().getSuperEntity());
        assertEquals("GrandParent", child.getSuperEntity().getSuperEntity().getName());
    }

    /**
     * Test : collection de références
     */
    @Test
    void testCollectionOfReferences() {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Root model="#1">
                    <Model id="#1" name="CollectionRefTest"/>
                    <Entity id="#2" model="#1" name="Student" />
                    <Attribute id="#3" entity="#2" name="name" type="String" />
                    <Reference id="#4" name="Student" entity="#2" />
                    <List id="#5" ref="#4" min="0" max="*" />
                    <Entity id="#6" model="#1" name="Classroom" />
                    <Attribute id="#7" entity="#6" name="students" type="#5" />
                </Root>
                """;
        
        Model model = (Model) analyser.getModelFromString(xmlContent);
        
        assertNotNull(model);
        Entity classroom = model.getEntities().stream()
                .filter(e -> "Classroom".equals(e.getName()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(classroom);
        Attribute studentsAttr = classroom.getAttributes().get(0);
        assertTrue(studentsAttr.getType() instanceof ListType);
        
        ListType listType = (ListType) studentsAttr.getType();
        assertTrue(listType.getElementType() instanceof ResolvedReference);
    }

    /**
     * Test : modèle sans nom
     */
    @Test
    void testModelWithoutName() {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Root model="#1">
                    <Model id="#1" />
                    <Entity id="#2" model="#1" name="TestEntity" />
                </Root>
                """;
        
        Model model = (Model) analyser.getModelFromString(xmlContent);
        
        assertNotNull(model);
        assertTrue(model.getName() == null || model.getName().isEmpty());
    }

    /**
     * Test : entité sans attributs
     */
    @Test
    void testEntityWithoutAttributes() {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Root model="#1">
                    <Model id="#1" name="EmptyEntityTest"/>
                    <Entity id="#2" model="#1" name="EmptyEntity" />
                </Root>
                """;
        
        Model model = (Model) analyser.getModelFromString(xmlContent);
        
        assertNotNull(model);
        Entity entity = model.getEntities().get(0);
        assertEquals("EmptyEntity", entity.getName());
        assertEquals(0, entity.getAttributes().size());
    }
}
