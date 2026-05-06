/**
 * 
 */
package fr.n7.stl.minic.ast.expression.accessible;

import fr.n7.stl.minic.ast.SemanticsUndefinedException;
import fr.n7.stl.minic.ast.expression.assignable.AssignableExpression;
import fr.n7.stl.minic.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.minic.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.type.PointerType;
import fr.n7.stl.minic.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;

/**
* Implementation of the Abstract Syntax Tree node for accessing an expression address.
* @author Marc Pantel
*
*/
public class AddressAccess implements AccessibleExpression {

	protected AssignableExpression assignable;

	public AddressAccess(AssignableExpression _assignable) {
		this.assignable = _assignable;
	}
	
	@Override
	public String toString() {
		return "& " + this.assignable.toString();
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope) {
		return assignable.collectAndPartialResolve(_scope);	
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> _scope) {
		return assignable.completeResolve(_scope);	
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		return new PointerType(assignable.getType());
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment f = _factory.createFragment();
		if (assignable instanceof PointerAccess || assignable instanceof ArrayAccess){
			return assignable.getCode(_factory);
		}
		if(assignable instanceof IdentifierAccess){
			IdentifierAccess id = (IdentifierAccess) this.assignable;

			if(id.expression instanceof VariableAccess){
				VariableDeclaration decl = (VariableDeclaration) ((VariableAccess) id.expression).getDeclaration();
				f.add(_factory.createLoadA(decl.getRegister(), decl.getOffset()));
				return f;
			}

			else if (id.expression instanceof ParameterAccess){
				ParameterDeclaration decl = (ParameterDeclaration)((ParameterAccess) id.expression).getDeclaration();
				f.add(_factory.createLoadA(Register.LB, decl.getOffset()));
				return f;
			}
		}
		if (this.assignable instanceof FieldAccess) {
        return ((FieldAccess) this.assignable).getCode(_factory);
    }
	return f;
	}


}
