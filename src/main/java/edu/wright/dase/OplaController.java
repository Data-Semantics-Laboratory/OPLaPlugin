package edu.wright.dase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.find.OWLEntityFinder;
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

public class OplaController
{
	private OWLModelManager				modelManager;
	private OWLOntology					owlOntology;
	private OWLDataFactory				owlDataFactory;
	private OWLEntityFinder				owlEntityFinder;
	private Set<OWLAnnotationProperty>	oplaAnnotations;
	private List<OWLAnnotationProperty>	oplaList;

	public OplaController(OWLModelManager modelManager)
	{
		this.modelManager = modelManager;
		this.update();
		this.oplaList = Arrays.asList(owlDataFactory.getOWLAnnotationProperty(IRI.create("isNativeTo")),
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
		this.oplaAnnotations = new HashSet<>(oplaList);
		//this.oplaAnnotations =  new HashSet<OWLAnnotationProperty>();
	}

	public void update()
	{
		this.owlOntology = this.modelManager.getActiveOntology();
		this.owlDataFactory = owlOntology.getOWLOntologyManager().getOWLDataFactory();
		this.owlEntityFinder = this.modelManager.getOWLEntityFinder();
	}

	public OWLEntity[] retrieve(String option)
	{
		if(option.equals("Classes"))
		{
			return this.retrieveClasses();
		}
		else if(option.equals("Individuals"))
		{
			return this.retrieveIndividuals();
		}
		else if(option.equals("Object Properties"))
		{
			return this.retrieveObjectProperties();
		}
		else if(option.equals("Data Properties"))
		{
			return this.retrieveDataProperties();
		}
		else if(option.equals("Data Types"))
		{
			return this.retrieveDataTypes();
		}
		else if(option.equals("Annotations"))
		{
			return this.retrieveAnnotations();
		}
		else if(option.equals("Class Axiom"))
		{
			return this.retrieveClasses();
		}
		else
		{
			return null;
		}
	}
	
	public List<OWLEntity> filter(OWLEntity[] entities)
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

	private OWLClass[] retrieveClasses()
	{
		Set<OWLClass> set = this.owlOntology.getClassesInSignature();
		OWLClass[] owlArr = set.toArray(new OWLClass[set.size()]);
		return owlArr;
	}

	private OWLObjectProperty[] retrieveObjectProperties()
	{
		Set<OWLObjectProperty> set = this.owlOntology.getObjectPropertiesInSignature();
		OWLObjectProperty[] owlArr = set.toArray(new OWLObjectProperty[set.size()]);
		return owlArr;
	}

	private OWLNamedIndividual[] retrieveIndividuals()
	{
		Set<OWLNamedIndividual> set = this.owlOntology.getIndividualsInSignature();
		OWLNamedIndividual[] owlArr = set.toArray(new OWLNamedIndividual[set.size()]);
		return owlArr;
	}

	private OWLDataProperty[] retrieveDataProperties()
	{
		Set<OWLDataProperty> set = this.owlOntology.getDataPropertiesInSignature();
		OWLDataProperty[] owlArr = set.toArray(new OWLDataProperty[set.size()]);
		return owlArr;
	}

	private OWLDatatype[] retrieveDataTypes()
	{
		Set<OWLDatatype> set = this.owlOntology.getDatatypesInSignature();
		OWLDatatype[] owlArr = set.toArray(new OWLDatatype[set.size()]);
		return owlArr;
	}

	private OWLAnnotationProperty[] retrieveAnnotations()
	{
		Set<OWLAnnotationProperty> set = this.owlOntology.getAnnotationPropertiesInSignature();
		OWLAnnotationProperty[] owlArr = set.toArray(new OWLAnnotationProperty[set.size()]);
		return owlArr;
	}
	/*
	 * private OWLClass[] retrieveClassAxioms() { Set<OWLObjectProperty> set =
	 * this.owlOntology.getObjectPropertiesInSignature(); OWLClass[] owlArr =
	 * set.toArray(new OWLClass[set.size()]); return owlArr; }
	 */

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
