package edu.wright.dase;

import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLClass;
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
	
	public String[] retireveClasses(OWLModelManager modelManager)
	{
		Set<OWLClass> set = this.owlOntology.getClassesInSignature();
		OWLClass[] owlArr = set.toArray(new OWLClass[set.size()]);
		String[] stringArr = new String[owlArr.length];
		for(int i = 0; i < stringArr.length; i++)
		{
			stringArr[i] = owlArr[i].toString();
		}
		return stringArr;
	}

	public OWLModelManager getModelManager()
	{
		return modelManager;
	}

	public void setModelManager(OWLModelManager modelManager)
	{
		this.modelManager = modelManager;
	}

}
