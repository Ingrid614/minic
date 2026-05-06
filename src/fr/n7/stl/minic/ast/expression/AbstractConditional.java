/**
 * 
 */
package fr.n7.stl.minic.ast.expression;

import fr.n7.stl.minic.ast.SemanticsUndefinedException;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.type.AtomicType;
import fr.n7.stl.minic.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Abstract Syntax Tree node for a conditional expression.
 * @author Marc Pantel
 *
 */
public class AbstractConditional<ExpressionKind extends Expression> implements Expression {

	/**
	 * AST node for the expression whose value is the condition for the conditional expression.
	 */
	protected Expression condition;
	
	/**
	 * AST node for the expression whose value is the then parameter for the conditional expression.
	 */
	protected ExpressionKind thenExpression;
	
	/**
	 * AST node for the expression whose value is the else parameter for the conditional expression.
	 */
	protected ExpressionKind elseExpression;
	
	/**
	 * Builds a binary expression Abstract Syntax Tree node from the left and right sub-expressions
	 * and the binary operation.
	 * @param _left : Expression for the left parameter.
	 * @param _operator : Binary Operator.
	 * @param _right : Expression for the right parameter.
	 */
	public AbstractConditional(Expression _condition, ExpressionKind _then, ExpressionKind _else) {
		this.condition = _condition;
		this.thenExpression = _then;
		this.elseExpression = _else;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope) {
		boolean ok = true;
		ok &= this.condition.collectAndPartialResolve(_scope);
		ok &= this.thenExpression.collectAndPartialResolve(_scope);
		ok &= this.elseExpression.collectAndPartialResolve(_scope);
		return ok;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> _scope) {
		boolean ok = true;
		ok &= this.condition.completeResolve(_scope);
		ok &= this.thenExpression.completeResolve(_scope);
		ok &= this.elseExpression.completeResolve(_scope);
		return ok;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.condition + " ? " + this.thenExpression + " : " + this.elseExpression + ")";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		if (this.thenExpression.getType().compatibleWith(this.elseExpression.getType())) {
			return this.thenExpression.getType();
		} else {
			return AtomicType.ErrorType; // ou null selon ton projet
		}
		// throw new SemanticsUndefinedException( "Semantics getType is undefined in ConditionalExpression.");
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {

		Fragment fragment = _factory.createFragment();

		String elseLabel =
			"else_expr_" + _factory.createLabelNumber();

		String endLabel =
			"end_expr_" + _factory.createLabelNumber();

		// Évaluation de la condition
		fragment.append(this.condition.getCode(_factory));

		// Si faux -> else
		fragment.add(
			_factory.createJumpIf(elseLabel, 0)
		);

		// THEN
		fragment.append(
			this.thenExpression.getCode(_factory)
		);

		// Saut vers la fin
		fragment.add(
			_factory.createJump(endLabel)
		);

		// ELSE
		fragment.addPrefix(elseLabel);

		fragment.append(
			this.elseExpression.getCode(_factory)
		);

		// FIN
		fragment.addPrefix(endLabel);

		return fragment;
	}

}
