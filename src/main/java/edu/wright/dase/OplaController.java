package edu.wright.dase;

import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
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
	
	public OWLClass[] retireveClasses(OWLModelManager modelManager)
	{
		Set<OWLClass> set = this.owlOntology.getClassesInSignature();
		OWLClass[] owlArr = set.toArray(new OWLClass[set.size()]);
		return owlArr;
	}
	public OWLObjectProperty[] retireveObjectProperties(OWLModelManager modelManager)
	{
		Set<OWLObjectProperty> set = this.owlOntology.getObjectPropertiesInSignature();
		OWLObjectProperty[] owlArr = set.toArray(new OWLObjectProperty[set.size()]);
		return owlArr;
	}
	public OWLNamedIndividual[] retireveIndividuals(OWLModelManager modelManager)
	{
		Set<OWLNamedIndividual> set = this.owlOntology.getIndividualsInSignature();
		OWLNamedIndividual[] owlArr = set.toArray(new OWLNamedIndividual[set.size()]);
		return owlArr;
	}
	public OWLDataProperty[] retireveDataProperties(OWLModelManager modelManager)
	{
		Set<OWLDataProperty> set = this.owlOntology.getDataPropertiesInSignature();
		OWLDataProperty[] owlArr = set.toArray(new OWLDataProperty[set.size()]);
		return owlArr;
	}
	public OWLDatatype[] retireveDataTypes(OWLModelManager modelManager)
	{
		Set<OWLDatatype> set = this.owlOntology.getDatatypesInSignature();
		OWLDatatype[] owlArr = set.toArray(new OWLDatatype[set.size()]);
		return owlArr;
	}
	public OWLAnnotationProperty[] retireveAnnotations(OWLModelManager modelManager)
	{
		Set<OWLAnnotationProperty> set = this.owlOntology.getAnnotationPropertiesInSignature();
				OWLAnnotationProperty[] owlArr = set.toArray(new OWLAnnotationProperty[set.size()]);
		return owlArr;
	}
/*	public OWLClass[] retireveClassAxioms(OWLModelManager modelManager)
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
