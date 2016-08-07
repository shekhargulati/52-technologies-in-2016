Groovy AST Transformations By Example
-----

Welcome to the thirty-second post of [52-technologies-in-2016](https://github.com/shekhargulati/52-technologies-in-2016) blog series. This week I learnt about Groovy AST transformations. AST transformations allows you to hook into the Groovy compilation process so that you can customize it to meet your needs. This is done at compilation time so you don't pay any cost at runtime. AST transformation is compile-time meta programming. If you have used Groovy you would know that you don't have to write commonly used methods like setters, getters, equals, hashcode, toString, etc. because Groovy can generate them for you. You just apply annotations to your class and voila Groovy add those methods for you. This helps you get rid of the boilerplate code and makes your code clean and readable. AST transformations are very easy to write. You don't have to be a compiler or AST ninja to write an AST transformation. In this blog, you will learn how to write an AST transformation that will add a `toHash` method to a class. `toHash` method will generate a hash for your object. You will be able to provide hash algorithm of your choice. We will use Java's `java.security.MessageDigest` to generate the hash code.

Before we write our own AST transformation let's look at AST transformation that you see every day. Let's suppose we have a simple Groovy class `Book` that has two fields `title` and `author`.

```groovy
class Book {

    String title
    String author
}
```

After you compile this code, you will be able to call setters and getters on `Book` object. The setters and getters were added by Groovy using AST transformation.

```groovy
class BookTest {

    @Test
    void 'should be able to set author and title of a book'() {

        def book = new Book()
        book.setTitle("OpenShift Cookbook")
        book.setAuthor("Shekhar Gulati")

        assertThat(book.getTitle(), equalTo("OpenShift Cookbook"))
        assertThat(book.getAuthor(), equalTo("Shekhar Gulati"))

    }
}
```

If you look at the decompiled code you will see setters and getters.

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package playground;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class Book implements GroovyObject {
    private String title;
    private String author;

    public Book(String title, String author) {
        CallSite[] var3 = $getCallSiteArray();
        MetaClass var4 = this.$getStaticMetaClass();
        this.metaClass = var4;
        this.title = (String)ShortTypeHandling.castToString(title);
        this.author = (String)ShortTypeHandling.castToString(author);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String var1) {
        this.title = var1;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String var1) {
        this.author = var1;
    }
}
```

Groovy supports two types of AST transformations -- Global and Local. Global AST transformations apply transformation to each and every class in your project where as local transformation apply transformation to only classes that are declared with an annotation. The transformation that we will write is a local transformation.

Let's write a simple test case in the `playground` package that will drive our code.

```groovy
package playground

import org.junit.Test
import static groovy.test.GroovyAssert.assertScript


class HashAstTransformationTests {


    @Test
    void "class with @Hash annotation should have toHash method that covert to SHA1 hash"(){
        assertScript('''

        import playground.Hash

        @Hash
        class Foo{
            String msg

            Foo(String msg){
                this.msg = msg
            }

            String toString(){
                this.msg
            }
        }

        def hash = new Foo("Hello, World!").toHash()
        assert hash == "0a0a9f2a6772942557ab5355d76af442f8f65e01"
    ''')
    }
}
```

In the code shown above, we have written a test case that will execute a script and test whether SHA1 hash of `Hello, World!` string is equal to `0a0a9f2a6772942557ab5355d76af442f8f65e01`.
In our test script, we created a simple Groovy class `Foo` and annotated it with `Hash` annotation. Local transformation works by defining annotations on the classes of interest.

When you will run this you will get error because `Hash` annotation does not exit.

Let's create a Java annotation called `Hash` in the `playground` package. It is recommend that you write AST transformations in Java.

```java
package playground;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Hash {

    String algorithm() default "SHA1";
}
```

It is a standard Java annotation. We have declared `Retention` to be `RetentionPolicy.SOURCE` because we don't need this annotation in the compiled code. In the compiled code, we will have the generated methods. `Target` for this annotation is `ElementType.TYPE` as we will declare this annotation on a class. Also, we have defined annotation attribute `algorithm` that will help user decide which hashing algorithm to choose. By default, we will use `SHA1` algorithm.


Let's rerun the test again to see the next failure. This time you will be greeted by exception message shown below. This error message clearly says that there is no `toHash` method in the `Foo` class.

```text
groovy.lang.MissingMethodException: No signature of method: Foo.toHash() is applicable for argument types: () values: []
```

Next step is to write an AST transformation that will add the `toHash` method. To do that, let's create class `ToHashAdderAstTransformation` in the `src/main/java/playground` package.

```java
package playground;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.control.CompilePhase;

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class ToHashAdderAstTransformation extends AbstractASTTransformation {

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
      System.out.println(String.format("Running AST transformation for class %s", ((ClassNode) nodes[1]).getName()));
    }
}
```


Let's understand the code written above.

1. You created a Java class `ToHashAdderAstTransformation` that extends `AbstractASTTransformation`. Groovy will instantiate and invoke all classes that are implementation of `ASTTransformation` interface.
2. You annotated the `ToHashAdderAstTransformation` class with `GroovyASTTransformation` annotation so that Groovy will know which `CompilePhase` to run your AST transformation in. There are nine compile phases -- `INITIALIZATION`, `PARSING`, `CONVERSION`, `SEMANTIC_ANALYSIS`, `CANONICALIZATION`, `INSTRUCTION_SELECTION`,`CLASS_GENERATION`, `OUTPUT`, and  `FINALIZATION`. We used `SEMANTIC_ANALYSIS` phase as it is the first phase in which local transformation can be applied.
3. Then, we implemented `visit` method. AST transformations are implemented using the visitor design pattern that's why method name is visit. This method will be invoked when AST transformation will be active. In the method implementation, we just added print statement. The print statement will render name of the class on which transformation will be applied.

If you run the test case now, you will not see `println` statement. The reason for this is that we have not enabled our AST transformation. To enable AST transformation, you have to declare that using the `GroovyASTTransformationClass` annotation as shown below.

```java
package playground;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@GroovyASTTransformationClass(classes = {ToHashAdderAstTransformation.class})
public @interface Hash {

    String algorithm() default "SHA1";
}
```

Run the test case again and this time you will see  the message printed to console.

```text
Running AST transformation for class Foo
```

So, we are moving in the right direction. Let's now write the proper implementation of visit method. The implementation will add `toHash` method to the generated class. Replace the code in the `ToHashAdderAstTransformation` with the code shown below.

```java
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

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
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
```

Let's understand the code shown above:

1. First, we grab the first two nodes from the `nodes` array. The first node provides information about the annotation and second node is a ClassNode providing information about the class on which annotation was declared.
2. Then, we defined code to inject in a String. We created a `toHash` method that uses Java `MessageDigest` class to convert a value to a hash.
3 Then, we converted the String script to list of AST nodes.
4. Finally, we extracted the first two methods and added them to the `ClassNode`. This will make sure both `toHash` and `toHexString` methods are added to the generated code.

Now, run your test case and it will pass.

------

That's all for this week. Please provide your valuable feedback by adding a comment to [https://github.com/shekhargulati/52-technologies-in-2016/issues/45](https://github.com/shekhargulati/52-technologies-in-2016/issues/45).

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-2/shekhargulati/52-technologies-in-2016/32-groovy-ast-transformations)](https://github.com/igrigorik/ga-beacon)
