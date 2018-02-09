package edu.wright.dase;

import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLClass;

public class OplaController
{
	private OWLModelManager modelManager;

	public OplaController(OWLModelManager modelManager)
	{
		this.modelManager = modelManager;
	}

	public String[] retireveClasses(OWLModelManager modelManager)
	{
		String[] classesArray = this.modelManager.getActiveOntology().getClassesInSignature()
		        .toArray(new String[this.modelManager.getActiveOntology().getClassesInSignature().toArray().length]);
		return classesArray;
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
