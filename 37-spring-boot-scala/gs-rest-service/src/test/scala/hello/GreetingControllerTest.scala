package hello

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(classOf[SpringRunner])
@SpringBootTest
@AutoConfigureMockMvc
class GreetingControllerTest {

  @Autowired
  var mockMvc: MockMvc = _

  @Test
  def helloWorldMessageWhenNameParameterIsNotSet(): Unit = {
    mockMvc.perform(get("/greeting"))
      .andExpect(status().isOk)
      .andExpect(MockMvcResultMatchers.content().json("""{"id":1,"content":"Hello, World!"}"""))
  }

  @Test
  def helloUserWhenNameParameterIsSetToUser(): Unit = {
    mockMvc.perform(get("/greeting").param("name","User"))
      .andExpect(status().isOk)
      .andExpect(MockMvcResultMatchers.content().json("""{"id":2,"content":"Hello, User!"}"""))
  }



}
