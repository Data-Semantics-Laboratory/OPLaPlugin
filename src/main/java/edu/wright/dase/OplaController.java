package edu.wright.dase;

import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

public class OplaController
{
	private OWLModelManager modelManager;
	private OWLOntology     owlOntology;
	
	public OplaController(OWLModelManager modelManager)
	{
		this.modelManager = modelManager;
		this.owlOntology  = modelManager.getActiveOntology();
	}
	
	public void update()
	{
		this.owlOntology = this.modelManager.getActiveOntology();
	}
	
	public Object[] retrieve(String option)
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
			return new Object[] {"Error parsing option"};
		}
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
/*	private OWLClass[] retrieveClassAxioms()
	{
		Set<OWLObjectProperty> set = this.owlOntology.getObjectPropertiesInSignature();
		OWLClass[] owlArr = set.toArray(new OWLClass[set.size()]);
		return owlArr;
	}*/

	public OWLModelManager getModelManager()
	{
		return modelManager;
	}

	public void setModelManager(OWLModelManager modelManager)
	{
		this.modelManager = modelManager;
	}

}
