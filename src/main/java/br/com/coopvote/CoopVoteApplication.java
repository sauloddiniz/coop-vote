package br.com.coopvote;

import br.com.coopvote.agendamentos.FecharPautas;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CoopVoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoopVoteApplication.class, args);
    }

}
