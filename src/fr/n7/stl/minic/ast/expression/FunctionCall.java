/**
 * 
 */
package fr.n7.stl.minic.ast.expression;

import java.util.Iterator;
import java.util.List;

import fr.n7.stl.minic.ast.SemanticsUndefinedException;
import fr.n7.stl.minic.ast.expression.accessible.AccessibleExpression;
import fr.n7.stl.minic.ast.expression.accessible.IdentifierAccess;
import fr.n7.stl.minic.ast.instruction.declaration.FunctionDeclaration;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for a function call expression.
 * @author Marc Pantel
 *
 */
public class FunctionCall implements AccessibleExpression {

	/**
	 * Name of the called function.
	 * TODO : Should be an expression.
	 */
	protected String name;
	
	/**
	 * Declaration of the called function after name resolution.
	 * TODO : Should rely on the VariableUse class.
	 */
	protected FunctionDeclaration function;
	
	/**
	 * List of AST nodes that computes the values of the parameters for the function call.
	 */
	protected List<AccessibleExpression> arguments;
	
	/**
	 * @param _name : Name of the called function.
	 * @param _arguments : List of AST nodes that computes the values of the parameters for the function call.
	 */
	public FunctionCall(String _name, List<AccessibleExpression> _arguments) {
		this.name = _name;
		this.function = null;
		this.arguments = _arguments;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = ((this.function == null)?this.name:this.function) + "( ";
		Iterator<AccessibleExpression> _iter = this.arguments.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
		}
		while (_iter.hasNext()) {
			_result += " ," + _iter.next();
		}
		return  _result + ")";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#collect(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope) {
		if (!_scope.knows(name)) {
			Logger.error("Fonction inconnue dans la table des symboles");
			return false;
		}
		if (!(_scope.get(name) instanceof FunctionDeclaration)) {
			Logger.error(name + " n'est pas une fonction!");
			return false;
		}
		this.function = (FunctionDeclaration) _scope.get(name);
		if (arguments.size() != function.getParameters().size()) {
			Logger.error("Les paramètres de la fonction " + name + " ne sont pas en bon nombre.");
			return false;
		}
		boolean ok = true;
		for (int i = 0; i < arguments.size(); i++) {
			ok = ok && arguments.get(i).collectAndPartialResolve(_scope);
			if (arguments.get(i) instanceof IdentifierAccess){
				IdentifierAccess argIA = (IdentifierAccess) arguments.get(i);
				arguments.set(i, argIA);
				Declaration declaration = _scope.get(argIA.name);
				ok = ok && declaration.getType().compatibleWith(function.getParameters().get(i).getType());
			} else {
				ok = ok && arguments.get(i).getType().compatibleWith(function.getParameters().get(i).getType());
			}
		}
		if(!ok) {
			Logger.error("Les arguments de la fonction "+ name + " ne sont pas du bon type.");
		}
		return ok;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> _scope) {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		return this.function.getType();
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment f = _factory.createFragment();
		for(AccessibleExpression arg : this.arguments){
			f.append(arg.getCode(_factory));
		}
		f.add(_factory.createCall(this.name, Register.LB));
		return f;
	}

}
