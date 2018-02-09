package edu.wright.dase;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class OplaUI extends JPanel {
	
	private FlowLayout buttonLayout = new FlowLayout();
	private JRadioButton classes = new JRadioButton("Classes");
	private JRadioButton individuals = new JRadioButton("Individuals");
	private JRadioButton objProp = new JRadioButton("Object Properties");
	private JRadioButton dataProp = new JRadioButton("Data Properties");
	private JRadioButton dataType = new JRadioButton("Data Types");
	private JRadioButton annot = new JRadioButton("Annotations");
	private JRadioButton classAx = new JRadioButton("Class Axioms");
	private JComboBox<String> comboAnnotations = new JComboBox<String>();
	private ButtonGroup buttons = new ButtonGroup();
	private JTextField textField = new JTextField();
	private JButton save = new JButton("Save");
	
	public OplaUI() {
		
		JPanel f1 = new JPanel(new FlowLayout());
		f1.add(classes);
		buttons.add(classes);
		f1.add(individuals);
		buttons.add(individuals);
		f1.add(objProp);
		buttons.add(objProp);
		f1.add(dataProp);
		buttons.add(dataProp);
		f1.add(dataType);
		buttons.add(dataType);
		f1.add(annot);
		buttons.add(annot);
		f1.add(classAx);
		buttons.add(classAx);
		f1.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		JPanel f2 = new JPanel(new FlowLayout());
		String[] data = {"one", "two", "three", "four"};
		JList<String> myList = new JList<String>(data);
		JScrollPane scrollPane = new JScrollPane(myList);
		scrollPane.setPreferredSize(new Dimension(100, 100));
		f2.add(scrollPane);
		f2.add(comboAnnotations);
		comboAnnotations.addItem("isNative");
		comboAnnotations.addItem("isExternal");
		comboAnnotations.addItem("NONE");
		f2.add(textField);
		textField.setPreferredSize(new Dimension(100, 30));
		f2.add(save);
		f2.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		JPanel b = new JPanel(new BorderLayout());
		b.add(f1, BorderLayout.NORTH);
		b.add(f2, BorderLayout.SOUTH);
		
		for (Enumeration<AbstractButton> e = buttons.getElements(); e.hasMoreElements();) {
			JRadioButton jrb = (JRadioButton)e.nextElement();
			jrb.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent ie) {
					if(ie.getStateChange() == ItemEvent.SELECTED)
					{
						JOptionPane.showMessageDialog(null,
							    ((JRadioButton)ie.getSource()).getLabel());
					}
				}
			});
		}
	}
}
