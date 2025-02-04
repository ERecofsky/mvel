package org.mule.mvel2.optimizers.impl.refl.nodes;

import org.mule.mvel2.ParserContext;
import org.mule.mvel2.compiler.Accessor;
import org.mule.mvel2.compiler.AccessorNode;
import org.mule.mvel2.integration.VariableResolverFactory;
import org.mule.mvel2.optimizers.OptimizerFactory;

public class NullSafe implements AccessorNode {
  private AccessorNode nextNode;
  private char[] expr;
  private int start;
  private int offset;
  private ParserContext pCtx;
  private Accessor innerAccessor;

  public NullSafe(char[] expr, int start, int offset, ParserContext pCtx) {
    this.expr = expr;
    this.start = start;
    this.offset = offset;
    this.pCtx = pCtx;
  }

  public Object getValue(Object ctx, Object elCtx, VariableResolverFactory variableFactory) {
    if (ctx == null) return null;
    if(innerAccessor == null) {
      innerAccessor = OptimizerFactory.getAccessorCompiler(OptimizerFactory.SAFE_REFLECTIVE)
              .optimizeAccessor(pCtx, expr, start, offset, ctx, elCtx, variableFactory, true, ctx.getClass());
    }

    // This is intended to avoid an infinite recursion.
    // A null safe bean property should return null in
    // order to end the evaluation process when the
    // there is no key found.
    if (innerAccessor instanceof NullSafe) {
      innerAccessor = null;
      return null;
    }

    if (nextNode != null) {
      return nextNode.getValue(ctx, elCtx, variableFactory);
    }
    else {
      return innerAccessor.getValue(ctx, elCtx, variableFactory);
    }
  }

  public Object setValue(Object ctx, Object elCtx, VariableResolverFactory variableFactory, Object value) {
    if (ctx == null) return null;
    return nextNode.setValue(ctx, elCtx, variableFactory, value);
  }

  public AccessorNode getNextNode() {
    return nextNode;
  }

  public AccessorNode setNextNode(AccessorNode accessorNode) {
    return this.nextNode = accessorNode;
  }

  public Class getKnownEgressType() {
    return Object.class;
  }
}
