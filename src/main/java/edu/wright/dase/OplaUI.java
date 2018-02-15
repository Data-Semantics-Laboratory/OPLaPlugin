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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.semanticweb.owlapi.model.OWLEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OplaUI extends JPanel
{
	/** Bookkeeping */
	private static final long			serialVersionUID	= 1L;
	private final Logger				log	= LoggerFactory.getLogger(OplaUI.class);
	
	/** Panels! */
	private JPanel						entityPanel;
	private JPanel						editorPanel;
	/** The Controller */
	private OplaController				oplaController;

	private DefaultListModel<OWLEntity>	entityListModel;
	private JList<OWLEntity>			entityList;
	private JScrollPane					entityScrollPane;
	private JComboBox<String>			comboAnnotations;
	private JTextField					targetTextField;

	private ButtonGroup					buttons;

	private JCheckBox					filterCheckBox;

	public OplaUI(OplaController oplaController)
	{
		// Save a reference to the controller
		this.oplaController = oplaController;

		// Construct the default DLM
		this.entityListModel = new DefaultListModel<>();

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
		classes.setSelected(true);

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
						String selectedEntity = ((JRadioButton) ie.getSource()).getText();
						boolean isFiltered = filterCheckBox.isSelected();
						updateEntityList(selectedEntity, isFiltered);
					}
				}
			});
		}
	}

	private void updateEntityList(String selectedEntity, boolean isFiltered)
	{
		try
		{
			// Get the list of the required entities
			List<OWLEntity> retrievedEntities = oplaController.retrieve(selectedEntity, isFiltered);
			// clear the current list
			entityListModel.removeAllElements();
			// Add all the elements to the list model
			retrievedEntities.forEach(e ->entityListModel.addElement(e));
		}
		catch(ClassCastException e)
		{
			log.debug(e.getMessage());
		}

	}

	private String findSelectedEntityOption()
	{
		// Loop through the buttons of the button group
		// This is better than accessing the stupid selection model
		// See:
		// https://stackoverflow.com/questions/201287/how-do-i-get-which-jradiobutton-is-selected-from-a-buttongroup
		for(Enumeration<AbstractButton> e = buttons.getElements(); e.hasMoreElements();)
		{
			JButton button = (JButton) e.nextElement();
			if(button.isSelected())
			{
				return button.getText();
			}
		}

		// This will never be returned as there is a default radiobutton
		// selection
		return "";
	}

	private void createEditorPanel()
	{
		this.entityList = new JList<>();
		this.entityList.setModel(this.entityListModel);
		this.entityScrollPane = new JScrollPane(this.entityList);
		this.entityScrollPane.setPreferredSize(new Dimension(500, 300));

		this.filterCheckBox = new JCheckBox("View Only Un-annotated Entities");

		this.filterCheckBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent ie)
			{
				String selectedEntity = findSelectedEntityOption();
				boolean toFilter = ie.getStateChange() == ItemEvent.SELECTED;

				updateEntityList(selectedEntity, toFilter);
			}
		});

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
		this.editorPanel.add(filterCheckBox);
		this.editorPanel.add(comboAnnotations);
		this.editorPanel.add(targetTextField);
		this.editorPanel.add(saveButton);
	}
}
