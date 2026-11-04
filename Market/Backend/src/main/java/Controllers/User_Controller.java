package Controllers;

import com.market.backend_market.magazynKlientow.User_Repo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class User_Controller {
    User_Repo user_repo;
    public User_Controller(User_Repo user_repo) {
        this.user_repo = user_repo;
    }
    @GetMapping("/exist/{login}")
    public boolean exist(
            @PathVariable String login
    ) {
        return user_repo.existsByLogin(login);
    }
}
