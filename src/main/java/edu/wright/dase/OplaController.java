package edu.wright.dase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.find.OWLEntityFinder;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OplaController
{
	/** book keeping (literally) */
	private final Logger							log		= LoggerFactory.getLogger(OplaController.class);

	private final static String						prefix	= "opla";

	private HashMap<String, OWLAnnotationProperty>	oplaAnnotations;

	private OWLModelManager							modelManager;
	private OWLOntology								owlOntology;
	private OWLDataFactory							owlDataFactory;
	private OWLEntityFinder							owlEntityFinder;
	private PrefixDocumentFormat					pdf;

	public OplaController(OWLModelManager modelManager)
	{
		this.modelManager = modelManager;
		// set model dependent items
		this.update();
	}

	/** used to update data fields that are dependent on the modelManager */
	public void update()
	{
		log.info("[OplaController] Starting Update.");
		// Retrieve the active ontology
		this.owlOntology = this.modelManager.getActiveOntology();
		// Only continue with the update if there is an active ontology
		if(this.owlOntology != null)
		{
			// DataFactory is used to create axioms to add to the ontology
			this.owlDataFactory = owlOntology.getOWLOntologyManager().getOWLDataFactory();
			// The EntitiyFinder allows us to find existing entities within the
			// ontology
			this.owlEntityFinder = this.modelManager.getOWLEntityFinder();
			// The PrefixDocumentFormat allows us to access the namespaces
			// contained in the ontology
			this.pdf = (PrefixDocumentFormat) owlOntology.getOWLOntologyManager().getOntologyFormat(owlOntology);
			// Add the opla namespace to the ontology, if it isn't there.
			if(!this.pdf.containsPrefixMapping(prefix + ":")) // colon is
			                                                  // necessary
			{
				// Set the prefix
				this.pdf.setPrefix(prefix, "http://ontologydesignpatterns.org/opla#");
			}
			else
			{
				log.info("[OplaController] 'opla' namespace already present.");
			}
			// Create the list of annotation properties,
			// but only if it hasn't been done before.
			if(this.oplaAnnotations == null)
			{
				this.oplaAnnotations = createAnnotationPropertyList();
			}
			log.info("[OplaController] Active Ontology items updated.");
		}
		log.info("[OplaController] Update Completed.");
	}

	/** create the OWLAnnotation Properties that the plugin will use. */
	private HashMap<String, OWLAnnotationProperty> createAnnotationPropertyList()
	{
		// Names of all the properties inside of OPLa
		String[] props = { "isNativeTo", "ofExternalType", "reusesPatternAsTemplate", "specializationOfModule",
		        "generatlizationOfModule", "derivedFromModule", "hasRelatedModule", "specializationOfPattern",
		        "generatlizationOfPattern", "derivedFromPattern", "hasRelatedPattern" };
		// Create a hashmap that links the "name"
		// of the property to the property object
		// We do this in order to access the properties by name in the
		// makeAnnotation method
		HashMap<String, OWLAnnotationProperty> oplaAnnotationProperties = new HashMap<>();
		for(String prop : props)
		{
			// Create an IRI for the opla annotation property
			IRI iri = IRI.create(this.pdf.getPrefix(prefix + ":"), prop);
			// Generate the annotation property for the iri
			OWLAnnotationProperty annotationProperty = this.owlDataFactory.getOWLAnnotationProperty(iri);
			oplaAnnotationProperties.put(prop, annotationProperty);
		}
		return oplaAnnotationProperties;
	}

	public List<OWLEntity> retrieve(String selectedEntity)
	{
		List<OWLEntity> retrievedEntities;

		if(selectedEntity.equals("Classes"))
		{
			retrievedEntities = this.retrieveClasses();
		}
		else if(selectedEntity.equals("Individuals"))
		{
			retrievedEntities = this.retrieveIndividuals();
		}
		else if(selectedEntity.equals("Object Properties"))
		{
			retrievedEntities = this.retrieveObjectProperties();
		}
		else if(selectedEntity.equals("Data Properties"))
		{
			retrievedEntities = this.retrieveDataProperties();
		}
		else if(selectedEntity.equals("Data Types"))
		{
			retrievedEntities = this.retrieveDataTypes();
		}
		else if(selectedEntity.equals("Annotations"))
		{
			retrievedEntities = this.retrieveAnnotations();
		}
		else if(selectedEntity.equals("Class Axiom"))
		{
			retrievedEntities = this.retrieveClasses();
		}
		else
		{
			retrievedEntities = new ArrayList<>();
		}
		return retrievedEntities;
	}

	private boolean containsOplaAnnotation(OWLEntity e)
	{
		// Iterate through all the datatype properties
		for(OWLAnnotationProperty prop : oplaAnnotations.values())
		{
			// And return true if there is an oplaannotation for the entity
			if(e.containsEntityInSignature(prop))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * creates an annotationassertionaxiom to add to the ontology.
	 * 
	 * @param owlEntity
	 *            the entity about which the assertion is being made (the
	 *            subject)
	 * @param comboString
	 *            the annotation property
	 * @param textFieldString
	 *            of the annotation assertion
	 */
	public OWLAnnotationAssertionAxiom makeAnnotationAxiom(OWLEntity owlEntity, String comboString,
	        String textFieldString)
	{
		// Retrieve the annotation property from the annotation list.
		OWLAnnotationProperty owlAnnotationProperty = this.oplaAnnotations.get(comboString);
		// Use the entityFinder to get the Literal Datatype
		OWLDatatype owlDataType = this.owlEntityFinder.getOWLDatatype("rdfs:Literal");
		// Construct a literal using the textfieldString
		OWLLiteral owlLiteral = owlDataFactory.getOWLLiteral(textFieldString, owlDataType);
		// Create the annotation
		OWLAnnotation owlAnnotation = owlDataFactory.getOWLAnnotation(owlAnnotationProperty, owlLiteral);
		// Get the iri from the entity
		IRI temp = owlEntity.getIRI();
		// Make the assertion axiom using the above values
		OWLAnnotationAssertionAxiom annotationAssertionAxiom = owlDataFactory.getOWLAnnotationAssertionAxiom(temp,
		        owlAnnotation);
		// Return it
		return annotationAssertionAxiom;
	}

	/** add the AnnotationAssertionAxiom to the ontology. */
	public void addAnnotation(OWLEntity owlEntity, String comboString, String textFieldString)
	{
		// construct the annotation axiom from the items sent from the gui
		OWLAnnotationAssertionAxiom annotationAssertionAxiom = makeAnnotationAxiom(owlEntity, comboString,
		        textFieldString);
		// Create the ontologychange
		AddAxiom addAxiom = new AddAxiom(this.owlOntology, annotationAssertionAxiom);
		// Apply it!
		this.modelManager.applyChange(addAxiom);
	}

	/* *********************************************** */

	private List<OWLEntity> retrieveClasses()
	{
		Set<OWLClass> set = this.owlOntology.getClassesInSignature();
		return new ArrayList<OWLEntity>(set);
	}

	private List<OWLEntity> retrieveObjectProperties()
	{
		Set<OWLObjectProperty> set = this.owlOntology.getObjectPropertiesInSignature();
		return new ArrayList<OWLEntity>(set);
	}

	private List<OWLEntity> retrieveIndividuals()
	{
		Set<OWLNamedIndividual> set = this.owlOntology.getIndividualsInSignature();
		return new ArrayList<OWLEntity>(set);
	}

	private List<OWLEntity> retrieveDataProperties()
	{
		Set<OWLDataProperty> set = this.owlOntology.getDataPropertiesInSignature();
		return new ArrayList<OWLEntity>(set);
	}

	private List<OWLEntity> retrieveDataTypes()
	{
		Set<OWLDatatype> set = this.owlOntology.getDatatypesInSignature();
		return new ArrayList<OWLEntity>(set);
	}

	private List<OWLEntity> retrieveAnnotations()
	{
		Set<OWLAnnotationProperty> set = this.owlOntology.getAnnotationPropertiesInSignature();
		return new ArrayList<OWLEntity>(set);
	}

	public List<OWLAnnotationAssertionAxiom> retrieveEntityAnnotations(OWLEntity selectedEntity)
	{
		// Get all OWLAnnotationAssertionAxioms from the ontology
		// that specifically pertain to the selectedEntity
		Set<OWLAnnotationAssertionAxiom> annotations = this.owlOntology.getAnnotationAssertionAxioms(selectedEntity.getIRI());
		
		return new ArrayList<OWLAnnotationAssertionAxiom>(annotations);
	}
}
