/**
 * 
 */
package de.unirostock.sems.bives.cellml.parser;

import java.util.ArrayList;
import java.util.List;

import de.unirostock.sems.bives.cellml.exception.BivesCellMLParseException;
import de.unirostock.sems.bives.ds.MathML;
import de.unirostock.sems.bives.exception.BivesDocumentConsistencyException;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;


/**
 * The Class CellMLReactionSubstance representing a substance taking part in a reaction.
 *
 * @author Martin Scharm
 */
public class CellMLReactionSubstance
extends CellMLEntity
{
	public static final int ROLE_REACTANT = 1;
	public static final int ROLE_PRODUCT = 2;
	public static final int ROLE_CATALYST = 3;
	public static final int ROLE_INHIBITOR = 4;
	public static final int ROLE_ACTIVATOR= 5;
	public static final int ROLE_RATE= 6;
	public static final int ROLE_MODIFIER= 7;
	public static final int DIRECTION_FORWARD = 0;
	public static final int DIRECTION_REVERSE = 1;
	public static final int DIRECTION_BOTH = 2;
	
	/** The variable that corresponds to this substance. */
	private CellMLVariable variable;
	
	/** The roles it plays. */
	private List<Role> roles;
	
	/** The corresponding component that defines the reaction. */
	private CellMLComponent component;
	
	/**
	 * The Class Role.
	 */
	public class Role
	extends CellMLEntity
	{
		
		/** The role attribute must have a value of "reactant", "product", "catalyst", "activator", "inhibitor", "modifier", or "rate". */
		public int role;
		/**  The optional direction attribute may be used on <role> elements in reversible reactions. If defined, it must have a value of "forward", "reverse", or "both". Its value indicates the direction of the reaction for which the role is relevant. It has a default value of "forward". */
		public int direction;
		
		/** The optional delta_variable attribute indicates which variable is used to store the change in concentration of the species represented by the variable referenced by the current <variable_ref> element. */
		public CellMLVariable delta_variable;
		/**  The optional stoichiometry attribute stores the stoichiometry of the current variable relative to the other reaction participants. */
		public Double stoichiometry;
		
		/** The <role> elements may also contain <math> elements in the MathML namespace, which define equations using MathML. */
		public List<MathML> math;
		
		/**
		 * Instantiates a new role.
		 *
		 * @param model the model that contains this reaction
		 * @param node the corresponding document node in the XML tree
		 * @throws BivesCellMLParseException the bives cell ml parse exception
		 * @throws BivesDocumentConsistencyException the bives document consistency exception
		 */
		public Role (CellMLModel model, DocumentNode node) throws BivesCellMLParseException, BivesDocumentConsistencyException
		{
			super (node, model);
			direction = DIRECTION_FORWARD;
			delta_variable = null;
			stoichiometry = null;
			math = new ArrayList<MathML> ();
			
			role = resolveRole (node.getAttribute ("role"));
			
			if (node.getAttribute ("direction") != null)
				direction = resolveDirection (node.getAttribute ("direction"));
			
			if (node.getAttribute ("delta_variable") != null)
				delta_variable = component.getVariable (node.getAttribute ("delta_variable"));
			
			if (node.getAttribute ("stoichiometry") != null)
				try
				{
					stoichiometry = Double.parseDouble (node.getAttribute ("stoichiometry"));
				}
				catch (NumberFormatException ex)
				{
					throw new BivesCellMLParseException ("no proper stoichiometry: " + node.getAttribute ("stoichiometry"));
				}
			
			List<TreeNode> kids = node.getChildrenWithTag ("math");
			for (TreeNode kid : kids)
				math.add (new MathML ((DocumentNode) kid));
		}
	}
	
	/**
	 * Instantiates a new CellML reaction substance.
	 *
	 * @param model the model that defines this substance
	 * @param component the component that hosts the reaction
	 * @param node the corresponding node in the XML tree
	 * @throws BivesDocumentConsistencyException the bives document consistency exception
	 * @throws BivesCellMLParseException the bives cell ml parse exception
	 */
	public CellMLReactionSubstance (CellMLModel model, CellMLComponent component, DocumentNode node) throws BivesDocumentConsistencyException, BivesCellMLParseException
	{
		super (node, model);
		this.component = component;
		String var = node.getAttribute ("variable");
		if (var == null)
			throw new BivesCellMLParseException ("variable ref in reaction of component " + component.getName () + " doesn't define a variable. ("+var+", "+node.getXPath ()+")");
		variable = component.getVariable (var);
		if (variable == null)
			throw new BivesCellMLParseException ("variable ref in reaction of component " + component.getName () + " doesn't define a valid variable. ("+var+", "+node.getXPath ()+")");
		
		roles = new ArrayList<Role> ();
		
		List<TreeNode> kids = node.getChildrenWithTag ("role");
		for (TreeNode kid : kids)
			roles.add (new Role (model, (DocumentNode) kid));
	}
	
	/**
	 * Gets the corresponding CellML variable.
	 *
	 * @return the variable
	 */
	public CellMLVariable getVariable ()
	{
		return variable;
	}
	
	/**
	 * Gets the roles of this substance.
	 *
	 * @return the roles
	 */
	public List<Role> getRoles ()
	{
		return roles;
	}

	/**
	 * Resolve direction of this substance.
	 *
	 * @param direction the direction flag
	 * @return the direction as a string
	 * @throws BivesCellMLParseException the bives cell ml parse exception
	 */
	public static final String resolveDirection (int direction) throws BivesCellMLParseException
	{
		if (direction == DIRECTION_FORWARD)
			return "forward";
		if (direction == DIRECTION_REVERSE)
			return "reverse";
		if (direction == DIRECTION_BOTH)
			return "both";
		throw  new BivesCellMLParseException ("unknown direction: " + direction);
	}
	
	/**
	 * Resolve direction.
	 *
	 * @param direction the direction as a string
	 * @return the direction flag
	 * @throws BivesCellMLParseException the bives cell ml parse exception
	 */
	public static final int resolveDirection (String direction) throws BivesCellMLParseException
	{
		if (direction.equals ("forward"))
			return DIRECTION_FORWARD;
		if (direction.equals ("reverse"))
			return DIRECTION_REVERSE;
		if (direction.equals ("both"))
			return DIRECTION_BOTH;
		throw  new BivesCellMLParseException ("unknown direction: " + direction);
	}

	/**
	 * Resolve the role flag to get a textual representation.
	 *
	 * @param role the role flag
	 * @return the role as a string
	 * @throws BivesCellMLParseException the bives cell ml parse exception
	 */
	public static final String resolveRole (int role) throws BivesCellMLParseException
	{
		if (role == ROLE_REACTANT)
			return "reactant";
		if (role == ROLE_PRODUCT)
			return "product";
		if (role == ROLE_CATALYST)
			return "catalyst";
		if (role == ROLE_ACTIVATOR)
			return "activator";
		if (role == ROLE_INHIBITOR)
			return "inhibitor";
		if (role == ROLE_MODIFIER)
			return "modifier";
		if (role == ROLE_RATE)
			return "rate";
		throw  new BivesCellMLParseException ("unknown role: " + role);
	}
	
	/**
	 * Resolve role as a string to get the corresponding flag.
	 *
	 * @param role the role as a sting 
	 * @return the role flag
	 * @throws BivesCellMLParseException the bives cell ml parse exception
	 */
	public static final int resolveRole (String role) throws BivesCellMLParseException
	{
		if (role.equals ("reactant"))
			return ROLE_REACTANT;
		if (role.equals ("product"))
			return ROLE_PRODUCT;
		if (role.equals ("catalyst"))
			return ROLE_CATALYST;
		if (role.equals ("activator"))
			return ROLE_ACTIVATOR;
		if (role.equals ("inhibitor"))
			return ROLE_INHIBITOR;
		if (role.equals ("modifier"))
			return ROLE_MODIFIER;
		if (role.equals ("rate"))
			return ROLE_RATE;
		throw  new BivesCellMLParseException ("unknown role: " + role);
	}
}
