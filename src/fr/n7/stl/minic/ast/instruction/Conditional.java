/**
 * 
 */
package fr.n7.stl.minic.ast.instruction;

import java.util.Optional;

import fr.n7.stl.minic.ast.Block;
import fr.n7.stl.minic.ast.SemanticsUndefinedException;
import fr.n7.stl.minic.ast.expression.Expression;
import fr.n7.stl.minic.ast.instruction.declaration.FunctionDeclaration;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.type.AtomicType;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Implementation of the Abstract Syntax Tree node for a conditional instruction.
 * @author Marc Pantel
 *
 */
public class Conditional implements Instruction {

	protected Expression condition;
	protected Block thenBranch;
	protected Block elseBranch;

	public Conditional(Expression _condition, Block _then, Block _else) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = _else;
	}

	public Conditional(Expression _condition, Block _then) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "if (" + this.condition + " )" + this.thenBranch + ((this.elseBranch != null)?(" else " + this.elseBranch):"");
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope) {
		boolean result = true;
		result &= this.condition.collectAndPartialResolve(_scope);
		result &= this.thenBranch.collectAndPartialResolve(_scope);
		if(this.elseBranch != null){
			result &= this.elseBranch.collectAndPartialResolve(_scope);
		}
		return result;
		//throw new SemanticsUndefinedException( "Semantics collect is undefined in Conditional.");
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope, FunctionDeclaration _container) {
		return this.collectAndPartialResolve(_scope);
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> _scope) {
		boolean result = true;
		result &= this.condition.completeResolve(_scope);
		result &= this.thenBranch.completeResolve(_scope);
		if(this.elseBranch != null){
			result &= this.elseBranch.completeResolve(_scope);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		if(!(this.thenBranch.checkType())){
			return false;
		}
		if(this.elseBranch != null){
			if(!(this.elseBranch.checkType())){
				return false;
			}
		}
		if(!(this.condition.getType().compatibleWith(AtomicType.BooleanType))){
			Logger.error("Type error : condition must be boolean : " + condition.getType() + " instead of boolean");
			return false;
		}
		return true;
		//throw new SemanticsUndefinedException( "Semantics checkType is undefined in Conditional.");
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		int length;
		if(elseBranch == null){
			length = this.thenBranch.allocateMemory(_register, _offset);
		}else{
			length = Math.max(this.thenBranch.allocateMemory(_register, _offset), this.elseBranch.allocateMemory(_register, _offset));
		}
		return length;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		throw new SemanticsUndefinedException( "Semantics getCode is undefined in Conditional.");
	}

}
