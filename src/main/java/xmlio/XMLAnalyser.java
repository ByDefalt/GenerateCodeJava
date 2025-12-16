package xmlio;

import javax.xml.parsers.*;
import org.w3c.dom.*;

// IMPORTS DU METAMODELE
import metaModel.MinispecElement;
import metaModel.Model;
import metaModel.Entity;
import metaModel.Attribute;
import metaModel.types.*;

import java.io.*;
import java.util.*;

public class XMLAnalyser {

    protected Map<String, MinispecElement> minispecIndex;
    protected Map<String, Element> xmlElementIndex;

    // NOUVEAU : Set pour suivre la pile de récursion et détecter les cycles
    private Set<String> currentlyCreating;

    // Types primitifs autorisés
    private static final List<String> PRIMITIVE_TYPES = Arrays.asList(
            "String", "Integer", "int", "Boolean", "boolean", "Float", "float", "Double", "double", "Date", "void"
    );

    public XMLAnalyser() {
        this.minispecIndex = new HashMap<>();
        this.xmlElementIndex = new HashMap<>();
        this.currentlyCreating = new HashSet<>(); // Initialisation
    }

    public Model getModelFromInputStream(InputStream stream) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(stream);
            Element root = doc.getDocumentElement();

            indexXmlElements(root);

            String modelId = root.getAttribute("model");
            if (modelId != null && !modelId.isEmpty()) {
                return (Model) getOrCreateElement(modelId);
            }

            for (Element el : xmlElementIndex.values()) {
                if (el.getTagName().equals("Model")) {
                    return (Model) getOrCreateElement(el.getAttribute("id"));
                }
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // --- CŒUR DU SYSTEME (MODIFIÉ POUR CYCLES) ---

    protected MinispecElement getOrCreateElement(String id) {
        if (id == null || !id.startsWith("#")) return null;

        // 1. Si déjà en cache (création terminée), on retourne
        if (minispecIndex.containsKey(id)) {
            return minispecIndex.get(id);
        }

        // 2. DETECTION DE CYCLE : Si l'ID est déjà en cours de création dans la pile actuelle
        if (currentlyCreating.contains(id)) {
            reportError("CYCLE DÉTECTÉ : L'élément " + id + " dépend de lui-même (directement ou indirectement).");
            return null; // On coupe la boucle ici
        }

        // 3. Recherche de l'élément XML brut
        Element el = xmlElementIndex.get(id);
        if (el == null) {
            reportError("ID introuvable dans le XML : " + id);
            return null;
        }

        // 4. On marque l'ID comme "en cours de création"
        currentlyCreating.add(id);

        try {
            // 5. Instanciation (peut être récursive pour 'extend')
            MinispecElement createdObject = instantiateObject(el);

            // 6. Enregistrement et remplissage
            if (createdObject != null) {
                // On met dans l'index AVANT de remplir les détails (attributs) pour les références croisées non structurelles
                minispecIndex.put(id, createdObject);
                fillObjectDetails(createdObject, el);
            }
            return createdObject;

        } finally {
            // 7. TRES IMPORTANT : On retire l'ID de la liste "en cours" quoi qu'il arrive
            currentlyCreating.remove(id);
        }
    }

    private MinispecElement instantiateObject(Element e) {
        String tag = e.getTagName();
        String name = e.getAttribute("name");

        switch (tag) {
            case "Model":
                return new Model(name);

            case "Entity":
                String extendId = e.getAttribute("extend");
                Entity superEntity = null;

                // GESTION STRICTE DE L'HERITAGE
                if (extendId != null && !extendId.isEmpty()) {
                    if (extendId.startsWith("#")) {
                        // Appel récursif qui va déclencher la détection de cycle si nécessaire
                        MinispecElement parentObj = getOrCreateElement(extendId);

                        if (parentObj instanceof Entity) {
                            superEntity = (Entity) parentObj;
                        } else if (parentObj == null) {
                            // Message spécifique si null (soit introuvable, soit cycle détecté plus haut)
                            reportError("Impossible de résoudre l'héritage pour '" + name + "'. (ID inexistant ou cycle détecté sur " + extendId + ")");
                        } else {
                            reportError("L'entité '" + name + "' étend l'objet '" + extendId + "' qui n'est pas une Entity.");
                        }
                    } else {
                        reportError("Attribut 'extend' invalide pour l'entité '" + name + "'. Doit être un ID (#...), reçu : " + extendId);
                    }
                }

                return new Entity(name, superEntity, new ArrayList<>());

            case "Attribute":
                Type type = resolveType(e.getAttribute("type"));
                return new Attribute(name, type);

            case "Reference":
                String targetEntityId = e.getAttribute("entity");
                String refName = (name != null && !name.isEmpty()) ? name : "Unknown";

                if (targetEntityId != null && targetEntityId.startsWith("#")) {
                    MinispecElement target = getOrCreateElement(targetEntityId);

                    if (target instanceof Entity) {
                        return new ResolvedReference(refName, (Entity) target);
                    } else if (target == null) {
                        reportError("La Reference '" + refName + "' pointe vers l'entité '" + targetEntityId + "' qui n'existe pas ou contient une erreur.");
                    } else {
                        reportError("La Reference '" + refName + "' pointe vers '" + targetEntityId + "' qui n'est pas une Entity.");
                    }
                } else {
                    reportError("La Reference '" + refName + "' n'a pas d'attribut 'entity' valide.");
                }

                return new UnresolvedReference(refName);

            case "Bag":
            case "List":
            case "Set":
            case "Array":
                return createCollectionType(e, tag);

            default:
                return null;
        }
    }

    protected Type resolveType(String typeStr) {
        if (typeStr == null || typeStr.isEmpty()) return new SimpleType("void");

        if (typeStr.startsWith("#")) {
            MinispecElement el = getOrCreateElement(typeStr);

            if (el instanceof Type) {
                return (Type) el;
            } else if (el == null) {
                reportError("Type introuvable ou erreur de cycle sur l'ID '" + typeStr + "'.");
                return new SimpleType("Error_NotFound");
            } else {
                if (el instanceof Entity) {
                    reportError("L'attribut utilise directement l'entité '" + typeStr + "' comme type. Utilisez <Reference>.");
                    return new UnresolvedReference(((Entity) el).getName());
                }
                reportError("L'ID '" + typeStr + "' ne correspond pas à un Type valide.");
                return new SimpleType("Error_InvalidType");
            }
        }
        return new SimpleType(typeStr);
    }

    protected Type createCollectionType(Element e, String tagName) {
        String ofAttr = e.getAttribute("of");
        String refAttr = e.getAttribute("ref");

        Type elementType;

        if (refAttr != null && !refAttr.isEmpty()) {
            elementType = resolveType(refAttr);
        } else {
            elementType = resolveType(ofAttr);
        }

        Integer min = parseIntSafe(e.getAttribute("min"));
        Integer max = parseIntSafe(e.getAttribute("max"));
        Integer size = parseIntSafe(e.getAttribute("size"));
        if (size == null) size = 0;

        switch (tagName) {
            case "Array": return new ArrayType(elementType, size);
            case "List":  return new ListType(elementType, min, max);
            case "Set":   return new SetType(elementType, min, max);
            case "Bag":   return new BagType(elementType, min, max);
            default:      return new SimpleType("UnknownCollection");
        }
    }

    private void fillObjectDetails(MinispecElement obj, Element e) {
        if (obj instanceof Model) {
            Model model = (Model) obj;
            for (Map.Entry<String, Element> entry : xmlElementIndex.entrySet()) {
                Element childEl = entry.getValue();
                if ("Entity".equals(childEl.getTagName())
                        && e.getAttribute("id").equals(childEl.getAttribute("model"))) {
                    Entity childEntity = (Entity) getOrCreateElement(entry.getKey());
                    if (childEntity != null) model.getEntities().add(childEntity);
                }
            }
        } else if (obj instanceof Entity) {
            Entity entity = (Entity) obj;
            for (Map.Entry<String, Element> entry : xmlElementIndex.entrySet()) {
                Element childEl = entry.getValue();
                if ("Attribute".equals(childEl.getTagName())
                        && e.getAttribute("id").equals(childEl.getAttribute("entity"))) {
                    Attribute attr = (Attribute) getOrCreateElement(entry.getKey());
                    if (attr != null) entity.getAttributes().add(attr);
                }
            }
        }
    }

    private void reportError(String message) {
        System.err.println("[XML ERREUR] " + message);
    }

    private void indexXmlElements(Element el) {
        if (el.hasAttribute("id")) {
            this.xmlElementIndex.put(el.getAttribute("id"), el);
        }
        NodeList nodes = el.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) instanceof Element) {
                indexXmlElements((Element) nodes.item(i));
            }
        }
    }

    private Integer parseIntSafe(String s) {
        try {
            return (s != null && !s.isEmpty()) ? Integer.parseInt(s) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Model getModelFromString(String c) {
        return getModelFromInputStream(new ByteArrayInputStream(c.getBytes()));
    }

    public Model getModelFromFile(File f) {
        try {
            return getModelFromInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public Model getModelFromFilenamed(String f) {
        return getModelFromFile(new File(f));
    }
}