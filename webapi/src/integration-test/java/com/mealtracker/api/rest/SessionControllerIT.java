package com.mealtracker.api.rest;

import com.mealtracker.MealTrackerApplication;
import com.mealtracker.config.WebSecurityConfig;
import com.mealtracker.services.session.AccessToken;
import com.mealtracker.services.session.SessionInput;
import com.mealtracker.services.session.SessionService;
import com.mealtracker.services.user.UserService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.mealtracker.request.AppRequestBuilders.post;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {SessionController.class})
@ContextConfiguration(classes = {MealTrackerApplication.class, WebSecurityConfig.class})
@Tag("integration")
@Tag("controller")
public class SessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SessionService sessionService;

    @MockitoBean
    private UserService userService;

    private static SessionInput eq(SessionInput expectation) {
        return argThat(new SessionInputMatcher(expectation));
    }

    @Test
    public void generateToken_Anonymous_ValidCredential_ExpectTokenReturned() throws Exception {
        var credentials = validInput();
        when(sessionService.generateToken(eq(credentials))).thenReturn(AccessToken.jwt("<your token>"));

        mockMvc.perform(post("/v1/sessions").content(credentials))
                .andExpect(status().isOk())
                .andExpect(content().json("{'data':{'accessToken':'<your token>','tokenType':'Bearer'}}"));
    }

    SessionInput validInput() {
        return new SessionInput("helloworld@gmail.com", "tooStrongPassword");
    }

    static class SessionInputMatcher implements ArgumentMatcher<SessionInput> {

        private final SessionInput expectation;

        public SessionInputMatcher(SessionInput expectation) {
            this.expectation = expectation;
        }

        @Override
        public boolean matches(SessionInput actual) {
            return expectation.email().equals(actual.email()) && expectation.password().equals(actual.password());
        }
    }
}
