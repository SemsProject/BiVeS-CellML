Read CellML Documents 
======================

Parse a CellML Document 
------------------------

The easiest way to parse CellML documents is using the [validator](http://sems.uni-rostock.de/trac/bives-core/wiki/ModelValidator). It will validate the file and provides you with the occurred error if the document is not valid. However, if the document is valid you get the CellML model for free:

```java
// create a CellML validator
CellMLValidator validator = new CellMLValidator ();

// is that document valid?
if (!validator.validate (document))
	// if not: print the error (which is an exception)
	System.err.println (validator.getError ());

// get the document
CellMLDocument doc = validator.getDocument ();
```

You may provide the *```document```* as an XML string, a File object pointing to a file on the disk, a URL to a model on the internet, or an already parsed [TreeDocument](http://sems.uni-rostock.de/trac/xmlutils/wiki/HowTo#TreeDocument). Please read [ModelRetriever](http://sems.uni-rostock.de/trac/bives-core/wiki/ModelRetriever) to learn how to restrict the **access to local/remote files**. (see also [ParseExample.java](https://github.com/SemsProject/BiVeS-CellML/blob/master/src/test/java/de/unirostock/sems/ParseExample.java))

Extract use the model of a document 
------------------------------------

The API provides a lot of methods to interact with CellML models. Here is just a small example:

```java
// get model
CellMLModel model = doc.getModel ();

// get all components and a map: `name` -> `component`
HashMap<String, CellMLComponent> components = model.getComponents ();

// get all variables of the component with the name COMPONENT
// also return a map: `name` -> `variable`
HashMap<String, CellMLVariable> variables = components.get ("COMPONENT").getVariables ();

// get the variable with name VAR in component COMPONENT
CellMLVariable var = variables.get ("VAR");

// trace the interface connections back to the root variable
CellMLVariable rootVar = var.getRootVariable ();

// get the encapsulation hierarchy network
CellMLHierarchyNetwork hierarchy = model.getHierarchy ().getHierarchyNetwork ("encapsulation", "");

// get the hierarchy node of component COMPONENT
CellMLHierarchyNode hNode = hierarchy.getNode (components.get ("COMPONENT"));

// get its parent component in terms of encapsulation
CellMLHierarchyNode hNodeParent = hNode.getParent ();
// and its children
List<CellMLHierarchyNode> hNodeKids = hNode.getChildren ();
```

see also [ParseExample.java](https://github.com/SemsProject/BiVeS-CellML/blob/master/src/test/java/de/unirostock/sems/ParseExample.java)