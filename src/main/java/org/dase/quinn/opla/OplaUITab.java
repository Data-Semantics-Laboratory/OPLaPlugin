package org.dase.quinn.opla;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;

public class OplaUITab extends OWLWorkspaceViewsTab
{
	private static final long		serialVersionUID	= 1L;
	private final ODPTabListener	listener			= new ODPTabListener();

	@Override
	public void initialise()
	{
	}

	@Override
	public void dispose()
	{
		getOWLModelManager().removeListener(this.listener);
	}

	private void update()
	{
	}

	private class ODPTabListener implements OWLModelManagerListener
	{
		@Override
		public void handleChange(OWLModelManagerChangeEvent event)
		{
			if(event.getType() == EventType.ACTIVE_ONTOLOGY_CHANGED)
			{
				update();
			}
		}
	}

}
