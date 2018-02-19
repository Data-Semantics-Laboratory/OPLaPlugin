package edu.wright.dase;

import java.awt.BorderLayout;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OplaTab extends OWLWorkspaceViewsTab
{
	/** book keeping (literally) */
	private static final long		serialVersionUID	= 1L;
	private static final Logger		log					= LoggerFactory.getLogger(OplaTab.class);

	/** For use in detecting changes in the underlying ontology */
	private final OplaTabListener	oplaTabListener		= new OplaTabListener();
	/** For accessing the underlying ontology */
	private OWLModelManager			modelManager;

	/** View and Controller */
	private OplaUI					oplaUI;
	private OplaController			oplaController;

	/** Not sure what this is for, yet */
	private OWLEditorKit			owlEditorKit;

	@Override
	public void initialise()
	{
		// Set up
		super.initialise();
		setToolTipText("OplaAnnotate");
		this.modelManager = getOWLModelManager();
		this.owlEditorKit = getOWLEditorKit();

		// Ensure that there is a model manager before continuing.
		if(this.modelManager != null)
		{
			// Continue set up
			this.modelManager.addListener(this.oplaTabListener);
			this.oplaController = new OplaController(this.modelManager);

			// Construct and populate the layout
			setLayout(new BorderLayout());

			update();
		}
		else // output warning to log, do not initialize further
		{
			log.warn("OPLaTab initialization failed: no model manager");
		}
	}

	@Override
	public void dispose()
	{
		getOWLModelManager().removeListener(this.oplaTabListener);
	}

	private void update()
	{
		// If there is an active ontology
		if(this.modelManager.getActiveOntology() != null)
		{
			// Update the tab. Remove and reconstruct the tab, if necessary.
			if(this.oplaUI != null)
			{
				remove(this.oplaUI);
			}

			this.oplaUI = new OplaUI(this.oplaController);
			add(this.oplaUI, BorderLayout.CENTER);

			validate();

			// Update the controller
			this.oplaController.update();
		}
		else
		{
			log.warn("Abort Update: no active ontology.");
		}
	}

	private class OplaTabListener implements OWLModelManagerListener
	{
		@Override
		public void handleChange(OWLModelManagerChangeEvent event)
		{
			// If the underlying ontology has changed in some manner, update the
			// view.
			if(event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED) || (event.isType(EventType.ONTOLOGY_LOADED))
			        || (event.isType(EventType.ONTOLOGY_RELOADED)))
			{
				try
				{
					update();
				}
				catch(Exception e)
				{
					log.warn("Exception thrown in update method.");
					e.printStackTrace();
				}
			}
		}
	}

}
