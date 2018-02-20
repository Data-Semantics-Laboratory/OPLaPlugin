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

import org.semanticweb.owlapi.model.OWLEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OplaUI extends JPanel
{
	/** Bookkeeping */
	private static final long			serialVersionUID	= 1L;
	private final Logger				log					= LoggerFactory.getLogger(OplaUI.class);

	/** Panels! */
	private JPanel						entityPanel;
	private JPanel						editorPanel;
	/** The Controller */
	private OplaController				oplaController;

	private DefaultListModel<OWLEntity>	entityListModel;
	private JList<OWLEntity>			entityList;
	private DefaultListModel<OWLEntity>	annotationListModel;
	private JList<OWLEntity>			annotationList;
	private JScrollPane					entityScrollPane;
	private JScrollPane					annotationScrollPane;
	private JComboBox<String>			comboAnnotations;
	private JTextField					targetTextField;

	private ButtonGroup					buttons;

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

		// Construct top level panel
		setLayout(new BorderLayout());
		this.add(this.entityPanel, BorderLayout.NORTH);
		this.add(this.editorPanel, BorderLayout.CENTER);
	}

	private void createEntityPanel()
	{
		// Create the buttons
		JRadioButton classes = new JRadioButton("Classes");
		JRadioButton individuals = new JRadioButton("Individuals");
		JRadioButton objProp = new JRadioButton("Object Properties");
		JRadioButton dataProp = new JRadioButton("Data Properties");
		JRadioButton dataType = new JRadioButton("Data Types");
		JRadioButton annot = new JRadioButton("Annotations");
		JRadioButton classAx = new JRadioButton("Class Axioms");

		// Set the default Selection
		classes.setSelected(true); // TODO fire the update entity list,

		// Add the buttons to a buttongroup
		this.buttons = new ButtonGroup();
		this.buttons.add(classes);
		this.buttons.add(individuals);
		this.buttons.add(objProp);
		this.buttons.add(dataProp);
		this.buttons.add(dataType);
		this.buttons.add(annot);
		this.buttons.add(classAx);

		// Create the panel for options
		this.entityPanel = new JPanel();
		this.entityPanel.setLayout(new FlowLayout());
		this.entityPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		// Add the buttons to the panel
		this.entityPanel.add(classes);
		this.entityPanel.add(individuals);
		this.entityPanel.add(objProp);
		this.entityPanel.add(dataProp);
		this.entityPanel.add(dataType);
		this.entityPanel.add(annot);
		this.entityPanel.add(classAx);

		// Create the ItemListeners for the buttons
		for(Enumeration<AbstractButton> e = buttons.getElements(); e.hasMoreElements();)
		{
			JRadioButton jrb = (JRadioButton) e.nextElement();
			jrb.addItemListener(new ItemListener()
			{
				@Override
				public void itemStateChanged(ItemEvent ie)
				{
					if(ie.getStateChange() == ItemEvent.SELECTED)
					{
						entityList.clearSelection();
						String selectedEntity = ((JRadioButton) ie.getSource()).getText();
						updateEntityList(selectedEntity);
					}
				}
			});
		}
	}

	private void updateEntityList(String selectedEntity)
	{
		try
		{
			// Get the list of the required entities
			List<OWLEntity> retrievedEntities = oplaController.retrieve(selectedEntity);
			// clear the current list
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
		this.annotationList = new JList<>();
		this.annotationList.setModel(this.annotationListModel);

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
						OWLEntity selectedEntity = entityListModel.getElementAt(index);
						// Get the list of the required entities
						List<OWLEntity> retrievedAnnotations = oplaController.retrieveEntityAnnotations(selectedEntity);
						// clear the current list
						annotationListModel.removeAllElements();
						// Add all the elements to the list model
						retrievedAnnotations.forEach(e -> annotationListModel.addElement(e));
					}
					catch(ArrayIndexOutOfBoundsException e)
					{
						// Don't do anything, this prevents the error when
						// switching
						// entity views
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
		this.annotationScrollPane = new JScrollPane(this.annotationList);
		this.annotationScrollPane.setPreferredSize(new Dimension(500, 300));

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
			}
		});

		// Create the "editor" panel
		this.editorPanel = new JPanel();
		this.editorPanel.setLayout(new FlowLayout());
		this.editorPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		// Add everything to the panel
		this.editorPanel.add(this.entityScrollPane);
		this.editorPanel.add(this.annotationScrollPane);
		this.editorPanel.add(comboAnnotations);
		this.editorPanel.add(targetTextField);
		this.editorPanel.add(saveButton);
	}
}
