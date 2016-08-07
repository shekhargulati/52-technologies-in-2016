package playground;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import java.util.List;

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class ToHashAdderAstTransformation extends AbstractASTTransformation {

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        AnnotationNode hashAnnotationNode = (AnnotationNode) nodes[0];
        ConstantExpression hashProvider = (ConstantExpression) hashAnnotationNode.getMember("algorithm");
        ClassNode classNode = (ClassNode) nodes[1];
        System.out.println(String.format("Running AST transformation for class %s", classNode.getName()));
        String hashString = "import java.security.MessageDigest\n" +
                "\n" +
                "class %s {\n" +
                "    String toHash() {\n" +
                "        def hash = MessageDigest.getInstance('%s')\n" +
                "\n" +
                "hash.update(toString().bytes)\n" +
                "        toHexString(hash.digest())\n" +
                "    }\n" +
                "private String toHexString(byte[] bytes) {\n" +
                "        StringBuilder result = new StringBuilder()\n" +
                "        for (int i = 0; i < bytes.length; i++) {\n" +
                "            result.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)\n" +
                "                    .substring(1))\n" +
                "        }\n" +
                "        return result.toString()\n" +
                "    }" +
                "}";
        List<ASTNode> astNodes = new AstBuilder()
                .buildFromString(String.format(hashString, classNode.getName(), hashProvider != null ? hashProvider.getValue() : "SHA1"));
        List<MethodNode> methods = ((ClassNode) astNodes.get(1)).getMethods();
        MethodNode toHashMethod = methods.get(0);
        MethodNode toHexStringMethod = methods.get(1);
        classNode.addMethod(toHashMethod);
        classNode.addMethod(toHexStringMethod);
    }

}
