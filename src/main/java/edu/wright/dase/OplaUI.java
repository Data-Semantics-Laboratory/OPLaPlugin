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

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLEntity;

public class OplaUI extends JPanel
{
	/** Bookkeeping */
	private static final long			serialVersionUID	= 1L;

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

	private boolean						isFiltered;

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

		// TODO: set default selection
		// Add the buttons to a buttongroup
		ButtonGroup buttons = new ButtonGroup();
		buttons.add(classes);
		buttons.add(individuals);
		buttons.add(objProp);
		buttons.add(dataProp);
		buttons.add(dataType);
		buttons.add(annot);
		buttons.add(classAx);

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
						if(isFiltered)
						{
							OWLEntity[] arr = oplaController.retrieve(((JRadioButton) ie.getSource()).getText());
							entityListModel.removeAllElements();
							for(OWLEntity o : oplaController.filter(arr))
							{
								if(true)
								{
									entityListModel.addElement(o);
								}
							}
						}
						else
						{
							OWLEntity[] arr = oplaController.retrieve(((JRadioButton) ie.getSource()).getText());
							entityListModel.removeAllElements();
							for(OWLEntity o : arr)
							{
								entityListModel.addElement(o);
							}
						}
					}
				}
			});
		}
	}

	private void createEditorPanel()
	{
		this.entityList = new JList<>();
		this.entityList.setModel(this.entityListModel);
		this.entityScrollPane = new JScrollPane(this.entityList);
		this.entityScrollPane.setPreferredSize(new Dimension(500, 300));

		JCheckBox filter = new JCheckBox("View Only Un-annotated Entities");
		filter.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent ie)
			{
				if(ie.getStateChange() == ItemEvent.SELECTED)
				{
					isFiltered = true;
				}
				else if(ie.getStateChange() == ItemEvent.DESELECTED)
				{
					isFiltered = false;
				}
				else
				{
					// Trailing Else
				}
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
		this.editorPanel.add(filter);
		this.editorPanel.add(comboAnnotations);
		this.editorPanel.add(targetTextField);
		this.editorPanel.add(saveButton);
	}
}
