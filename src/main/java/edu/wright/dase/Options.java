package edu.wright.dase;

public enum Options
{
	CLASSES("Classes"), INDIVIDUALS("Individuals"), OBJECT_PROPERTIES("Object Properites"), DATA_PROPERTIES(
	        "Data Properties"), DATA_TYPES("Data Types"), ANNOTATIONS("Annotations"), CLASS_AXIOMS("Class Axioms");

	private final String option;

	Options(String option)
	{
		this.option = option;
	}

	private String option()
	{
		return option;
	}
}
