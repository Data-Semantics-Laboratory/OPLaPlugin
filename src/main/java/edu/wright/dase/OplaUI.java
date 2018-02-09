package edu.wright.dase;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class OplaUI extends JPanel
{
	/** Bookkeeping */
	private static final long	serialVersionUID	= 1L;


	/** Panels! */
	private JPanel				entityPanel;
	private JPanel				editorPanel;
	/** The Controller */
	private OplaController oplaController;
	
	public OplaUI(OplaController oplaController)
	{
		this.oplaController = oplaController;
		
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
		entityPanel.setLayout(new FlowLayout());
		entityPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		// Add the buttons to the panel
		entityPanel.add(classes);
		entityPanel.add(individuals);
		entityPanel.add(objProp);
		entityPanel.add(dataProp);
		entityPanel.add(dataType);
		entityPanel.add(annot);
		entityPanel.add(classAx);

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
						JOptionPane.showMessageDialog(null, ((JRadioButton) ie.getSource()).getText());
					}
				}
			});
		}
	}

	private void createEditorPanel()
	{
		// TODO: remove hardcoded list data and replace with default selection
		String[] data = { "one", "two", "three", "four" }; // this should be a call to the controller
		JList<String> entityList = new JList<String>(data);
		JScrollPane scrollPane = new JScrollPane(entityList);
		scrollPane.setPreferredSize(new Dimension(100, 100));
		
		// Create the dropdown menu
		JComboBox<String> comboAnnotations = new JComboBox<String>();
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
		JTextField targetTextField = new JTextField();
		targetTextField.setPreferredSize(new Dimension(100, 30));
		
		// Create the button for saving the annotation
		JButton saveButton = new JButton("Save");
		// TODO: add actionlistener for saving the annotation
		
		// Create the "editor" panel
		editorPanel.setLayout(new FlowLayout());
		editorPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		// Add everythin
		editorPanel.add(scrollPane);
		editorPanel.add(comboAnnotations);
		editorPanel.add(targetTextField);
		editorPanel.add(saveButton);
	}
}
