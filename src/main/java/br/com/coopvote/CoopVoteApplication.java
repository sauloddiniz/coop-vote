package br.com.coopvote;

import br.com.coopvote.agendamentos.FecharPautas;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CoopVoteApplication implements CommandLineRunner {

    private final FecharPautas fecharPautas;

    public CoopVoteApplication(FecharPautas fecharPautas) {
        this.fecharPautas = fecharPautas;
    }

    public static void main(String[] args) {
        SpringApplication.run(CoopVoteApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        fecharPautas.executarFechamentoDePautas();
    }
}
