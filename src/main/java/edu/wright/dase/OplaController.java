package edu.wright.dase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OplaController
{
	/** book keeping (literally) */
	private final Logger				log	= LoggerFactory.getLogger(OplaController.class);

	private Set<OWLAnnotationProperty>	oplaAnnotations;

	private OWLModelManager				modelManager;
	private OWLOntology					owlOntology;
	private OWLDataFactory				owlDataFactory;
	private OWLEntityFinder				owlEntityFinder;
	private PrefixDocumentFormat		pdf;

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
			if(!this.pdf.containsPrefixMapping("opla:")) // colon is necessary
			{
				// Set the prefix
				this.pdf.setPrefix("opla", "http://ontologydesignpatterns.org/opla");
				// Immediately apply the change
				try
				{
					this.modelManager.save();
					log.info("[OplaController] Added 'opla' namespace");
				}
				catch(OWLOntologyStorageException e)
				{
					log.error("[OplaController] Could not save the ontology.");
				}
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
	private HashSet<OWLAnnotationProperty> createAnnotationPropertyList()
	{
		List<OWLAnnotationProperty> oplaList = Arrays.asList(
		        owlDataFactory.getOWLAnnotationProperty(IRI.create("isNativeTo")),
		        owlDataFactory.getOWLAnnotationProperty(IRI.create("ofExternalType")),
		        owlDataFactory.getOWLAnnotationProperty(IRI.create("reusesPatternAsTemplate")),
		        owlDataFactory.getOWLAnnotationProperty(IRI.create("specializationOfModule")),
		        owlDataFactory.getOWLAnnotationProperty(IRI.create("generatlizationOfModule")),
		        owlDataFactory.getOWLAnnotationProperty(IRI.create("derivedFromModule")),
		        owlDataFactory.getOWLAnnotationProperty(IRI.create("hasRelatedModule")),
		        owlDataFactory.getOWLAnnotationProperty(IRI.create("specializationOfPattern")),
		        owlDataFactory.getOWLAnnotationProperty(IRI.create("generatlizationOfPattern")),
		        owlDataFactory.getOWLAnnotationProperty(IRI.create("derivedFromPattern")),
		        owlDataFactory.getOWLAnnotationProperty(IRI.create("hasRelatedPattern")));

		return new HashSet<>(oplaList);
	}

	public List<OWLEntity> retrieve(String selectedEntity, boolean isFiltered)
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

		if(isFiltered)
		{
			retrievedEntities = filter(retrievedEntities);
		}

		return retrievedEntities;

	}

	public List<OWLEntity> filter(List<OWLEntity> entities)
	{
		List<OWLEntity> filtered = new ArrayList<>();

		for(OWLEntity e : entities)
		{
			if(!containsOplaAnnotation(e))
			{
				filtered.add(e);
			}
		}

		return filtered;
	}

	private boolean containsOplaAnnotation(OWLEntity e)
	{
		for(OWLAnnotationProperty prop : oplaAnnotations)
		{
			if(e.containsEntityInSignature(prop))
			{
				return true;
			}
		}

		return false;
	}

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

	public OWLModelManager getModelManager()
	{
		return modelManager;
	}

	public void setModelManager(OWLModelManager modelManager)
	{
		this.modelManager = modelManager;
	}

	public OWLAnnotationAssertionAxiom makeAnnotationAxiom(OWLEntity owlEntity, String comboString,
	        String textFieldString)
	{
		OWLAnnotationProperty owlAnnotationProperty = owlDataFactory.getOWLAnnotationProperty(IRI.create(comboString));
		OWLDatatype owlDataType = this.owlEntityFinder.getOWLDatatype("rdfs:Literal");
		OWLLiteral owlLiteral = owlDataFactory.getOWLLiteral(textFieldString, owlDataType);
		OWLAnnotation owlAnnotation = owlDataFactory.getOWLAnnotation(owlAnnotationProperty, owlLiteral);
		IRI temp = owlEntity.getIRI();
		return owlDataFactory.getOWLAnnotationAssertionAxiom(temp, owlAnnotation);

	}

	public void applyDirectChange(OWLOntologyChange owlOntologyChange)
	{
		modelManager.applyChange(owlOntologyChange);
	}

	public void addAnnotation(OWLEntity owlEntity, String comboString, String textFieldString)
	{
		AddAxiom addAxiom = new AddAxiom(this.owlOntology,
		        makeAnnotationAxiom(owlEntity, comboString, textFieldString));
		applyDirectChange(addAxiom);
	}

}
