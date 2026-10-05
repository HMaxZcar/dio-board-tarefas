package br.com.dio.board;

import br.com.dio.board.config.ConnectionConfig;
import br.com.dio.board.config.DatabaseMigration;
import br.com.dio.board.ui.MainMenu;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Inicializando banco de dados...");
            DatabaseMigration.migrate();

            try (Connection connection = ConnectionConfig.getConnection()) {
                new MainMenu(connection).execute();
            }
        } catch (Exception e) {
            System.err.println("Não foi possível iniciar a aplicação.");
            System.err.println("Motivo: " + e.getMessage());
            System.err.println("Verifique se o MySQL está em execução e se as credenciais estão corretas.");
        }
    }
}
