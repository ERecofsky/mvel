package org.mule.mvel2.tests.core;

import junit.framework.TestCase;
import org.mule.mvel2.MVEL;
import org.mule.mvel2.ParserContext;

import java.util.HashMap;

/**
 * @author Mike Brock .
 */
public class ScopeTests extends TestCase {
  public void testNoScopeLeakageInterpreted() {
    String ex = "if (true) { var i = 0 }; i";

    try {
      MVEL.eval(ex, new HashMap<String, Object>());
      fail("should have failed");
    }
    catch (Exception e) {
      // good!
    }
  }

  public void testNoScopeLeakageCompiled() {
    String ex = "if (true) { var i = 0 }; i";

    try {
      MVEL.compileExpression(ex, ParserContext.create().stronglyTyped());
      fail("should have failed");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
