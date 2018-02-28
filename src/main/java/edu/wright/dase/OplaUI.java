package edu.wright.dase;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OplaUI extends JPanel
{
	/* Bookkeeping */
	private static final long								serialVersionUID	= 1L;
	private final Logger									log					= LoggerFactory.getLogger(OplaUI.class);
	private static final String[]							buttonLabels		= { "Ontology", "Classes",
	        "Individuals", "Object Properties", "Data Properties", "Data Types", "Annotations" };

	/* Panels! */
	private JPanel											entityPanel;
	private JPanel											editorPanel;
	private JPanel											annotationPanel;
	/* The Controller */
	private OplaController									oplaController;
	/* References to major gui pieces */
	private DefaultListModel<OWLEntity>						entityListModel;
	private JList<OWLEntity>								entityList;
	private DefaultListModel<OWLAnnotationAssertionAxiom>	annotationListModel;
	private JList<OWLAnnotationAssertionAxiom>				annotationList;
	private JScrollPane										entityScrollPane;
	private JScrollPane										annotationScrollPane;
	private JComboBox<String>								comboAnnotations;
	private JTextField										targetTextField;
	private ButtonGroup										buttons;
	private OWLEntity										selectedEntity;

	public OplaUI(OplaController oplaController)
	{
		// Save a reference to the controller
		this.oplaController = oplaController;

		// Construct the default DLM
		this.entityListModel = new DefaultListModel<>();
		this.annotationListModel = new DefaultListModel<>();

		// Populate the panels
		createEntityPanel();
		createEditorPanel();
		createAnnotationPanel();

		// Construct top level panel
		setLayout(new BorderLayout());
		this.add(this.entityPanel, BorderLayout.NORTH);
		this.add(this.editorPanel, BorderLayout.CENTER);
		this.add(this.annotationPanel, BorderLayout.SOUTH);
	}

	private void createEntityPanel()
	{
		// Initialize button group
		this.buttons = new ButtonGroup();
		// Create the panel for the Entity Options
		this.entityPanel = new JPanel();
		this.entityPanel.setLayout(new FlowLayout());
		this.entityPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		// Create all buttons for the Entity Options
		for(String label : buttonLabels)
		{
			// Create the button
			JRadioButton jrb = new JRadioButton(label);
			// Create an ItemListener for the button
			jrb.addItemListener(new ItemListener()
			{
				@Override
				public void itemStateChanged(ItemEvent ie)
				{
					if(ie.getStateChange() == ItemEvent.SELECTED)
					{
						// Add the annotations related to the chosen option
						String selectedEntity = ((JRadioButton) ie.getSource()).getText();
						updateEntityList(selectedEntity);
					}
				}
			});
			// Add the button to the button group
			this.buttons.add(jrb);
			// Add the button to the panel
			this.entityPanel.add(jrb);
		}
	}

	/**
	 * This method updates the entityListModel to display the axioms related to
	 * the selectedEntity
	 * 
	 * @param selectedEntity
	 *            alias for the entity type.
	 */
	private void updateEntityList(String selectedEntity)
	{
		try
		{
			// Get the list of the required entities
			List<OWLEntity> retrievedEntities = oplaController.retrieve(selectedEntity);
			// Clear the current list 
			// (this also removes everything from the scrollpane)
			entityListModel.removeAllElements();
			// Add all the elements to the list model
			retrievedEntities.forEach(e -> entityListModel.addElement(e));
		}
		catch(ClassCastException e)
		{
			log.debug(e.getMessage());
		}
	}

	private void createEditorPanel()
	{
		this.entityList = new JList<>();
		this.entityList.setModel(this.entityListModel);

		this.entityList.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent lse)
			{
				// prevents the functionality from firing twice
				// see documentation on listselectionevent
				if(!lse.getValueIsAdjusting())
				{
					try
					{
						int index = lse.getFirstIndex();
						selectedEntity = entityListModel.getElementAt(index);
						updateAnnotationList(selectedEntity);
					}
					catch(ArrayIndexOutOfBoundsException e)
					{
						// Don't do anything, this prevents the error when
						// switching entity views
					}
					catch(ClassCastException e)
					{
						log.debug(e.getMessage());
					}
				}
			}
		});

		this.entityScrollPane = new JScrollPane(this.entityList);
		this.entityScrollPane.setPreferredSize(new Dimension(500, 300));

		// Create the dropdown menu
		comboAnnotations = new JComboBox<String>();
		comboAnnotations.addItem("isNativeTo");
		comboAnnotations.addItem("ofExternalType");
		comboAnnotations.addItem("reusesPatternAsTemplate");
		comboAnnotations.addItem("specializationOfModule");
		comboAnnotations.addItem("generatlizationOfModule");
		comboAnnotations.addItem("derivedFromModule");
		comboAnnotations.addItem("hasRelatedModule");
		comboAnnotations.addItem("specializationOfPattern");
		comboAnnotations.addItem("generatlizationOfPattern");
		comboAnnotations.addItem("derivedFromPattern");
		comboAnnotations.addItem("hasRelatedPattern");

		// Create targetTextField
		targetTextField = new JTextField();
		targetTextField.setPreferredSize(new Dimension(100, 30));

		// Create the button for saving the annotation
		JButton saveButton = new JButton("Save");

		saveButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				OWLEntity owlEntity = entityList.getSelectedValue();
				String comboString = comboAnnotations.getSelectedItem().toString();
				String textFieldString = targetTextField.getText();
				oplaController.addAnnotation(owlEntity, comboString, textFieldString);
				updateAnnotationList(selectedEntity);
			}
		});

		// Create the "editor" panel
		this.editorPanel = new JPanel();
		this.editorPanel.setLayout(new FlowLayout());
		this.editorPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		// Add everything to the panel
		this.editorPanel.add(this.entityScrollPane);
		this.editorPanel.add(comboAnnotations);
		this.editorPanel.add(targetTextField);
		this.editorPanel.add(saveButton);
	}

	private void createAnnotationPanel()
	{
		this.annotationList = new JList<>();
		this.annotationList.setModel(this.annotationListModel);

		this.annotationScrollPane = new JScrollPane(this.annotationList);
		this.annotationScrollPane.setPreferredSize(new Dimension(500, 300));

		// Create the remove button
		JButton removeButton = new JButton("Remove");
		removeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				OWLAnnotationAssertionAxiom owlAxiom = annotationList.getSelectedValue();
				oplaController.removeAnnotation(owlAxiom);
				updateAnnotationList(selectedEntity);
			}
		});

		// Create the "annotation" panel
		this.annotationPanel = new JPanel();
		this.annotationPanel.setLayout(new FlowLayout());
		this.annotationPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		// Add everything to the "annotation" panel
		this.annotationPanel.add(this.annotationScrollPane);
		this.annotationPanel.add(removeButton);
	}

	private void updateAnnotationList(OWLEntity selectedEntity)
	{
		try
		{
			// Get the list of the required entities
			List<OWLAnnotationAssertionAxiom> retrievedAnnotations = oplaController
			        .retrieveEntityAnnotations(selectedEntity);
			// clear the current list
			annotationListModel.removeAllElements();
			// Add all the elements to the list model
			retrievedAnnotations.forEach(e -> annotationListModel.addElement(e));
		}
		catch(ClassCastException e)
		{
			log.debug(e.getMessage());
		}
	}
}
