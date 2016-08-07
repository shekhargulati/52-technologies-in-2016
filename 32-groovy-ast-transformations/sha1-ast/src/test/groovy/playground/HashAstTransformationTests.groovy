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

    @Test
    void "class with @Hash annotation should have toHash method that covert to MD5 hash"(){
        assertScript('''

        import playground.Hash

        @Hash(algorithm="MD5")
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
        assert hash == "65a8e27d8879283831b664bd8b7f0ad4"
    ''')

    }

}
