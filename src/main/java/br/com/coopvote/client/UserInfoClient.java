package br.com.coopvote.client;

import br.com.coopvote.dto.UserInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "userInfoClient",
        url = "https://user-info.herokuapp.com/users",
        configuration = FeignConfig.class)
public interface UserInfoClient {

    @GetMapping("/{cpf}")
    UserInfoResponseDto getInfo(@PathVariable("cpf") String cpf);
}
