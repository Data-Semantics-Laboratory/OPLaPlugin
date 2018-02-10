package edu.wright.dase;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;

import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.find.OWLEntityFinder;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OdpMainUITab extends OWLWorkspaceViewsTab
{
	/** book keeping (literally) */
	private static final long		serialVersionUID	= 1L;
	private static final Logger		log					= LoggerFactory.getLogger(OdpMainUITab.class);

	/** For use in detecting changes in the underlying ontology */
	private final OplaTabListener	oplaTabListener		= new OplaTabListener();

	private OWLModelManager			modelManager;
	/** Not sure what these are for, yet */
	private OWLEditorKit			owlEditorKit;
	private OWLEntityFinder			owlEntityFinder;

	private OplaUI					oplaUI;
	private OplaController			oplaController;

	@Override
	public void initialise()
	{
		// Set up
		super.initialise();
		setToolTipText("OplaAnnotate");

		this.modelManager = getOWLModelManager();

		// Ensure that there is a model manager before continuing.
		if(this.modelManager != null)
		{
			// Continue set up
			this.modelManager.addListener(this.oplaTabListener);

			// Construct and populate the layout
			setLayout(new BorderLayout());

			this.oplaController = new OplaController(this.modelManager);
			this.oplaUI = new OplaUI(this.oplaController);
			add(oplaUI, BorderLayout.CENTER);

			/*
			 * TODO: do more here add(new EditorMenuBar(editor),
			 * BorderLayout.NORTH);
			 * 
			 * JFrame mainWindow = (javax.swing.JFrame)
			 * SwingUtilities.windowForComponent(this);
			 * editor.setProtegeMainWindow(mainWindow);
			 */

			// If there is an active ontology
			if(this.modelManager.getActiveOntology() != null)
			{
				update();
			}
		}
		else // output warning to log, do not initialize further
		{
			log.warn("SWRLTab initialization failed - no model manager");
		}
	}

	@Override
	public void dispose()
	{
		super.dispose();
		getOWLModelManager().removeListener(this.oplaTabListener);
	}

	private void update()
	{
		if(this.oplaUI != null)
		{
			remove(this.oplaUI);
		}
		this.oplaUI = new OplaUI(this.oplaController);
		add(oplaUI, BorderLayout.CENTER);

	}

	private class OplaTabListener implements OWLModelManagerListener
	{
		@Override
		public void handleChange(OWLModelManagerChangeEvent event)
		{
			// If the underlying ontology has changed, change the view.
			if(event.getType() == EventType.ACTIVE_ONTOLOGY_CHANGED)
			{
				update();
			}
		}
	}

}
