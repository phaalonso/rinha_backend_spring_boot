package com.example.demo;

import com.example.demo.exceptions.NotFoundHttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PessoaRepository {
    private final Connection connection;
    private final JdbcTemplate jdbcTemplate;

    private static final String QUERY_INSERT_PESSOA = """
            insert into tb_people (id, name, nick, birth_date, stack)
            values (?, ?, ?, ?::date, ?)
            """;

    private static final String QUERY_FIND_PESSOA = """
            select * from tb_people
             where id = ?::uuid
            """;

    private static final String QUERY_COUNT = "SELECT COUNT(*) as qtd FROM tb_people";

    @Autowired
    public PessoaRepository(DataSource dataSource, JdbcTemplate jdbcTemplate) throws SQLException {
        this.connection = dataSource.getConnection();
        this.jdbcTemplate = jdbcTemplate;
    }

    public void inserePessoa(PessoaModel pessoaModel) throws SQLException {
        Object stack = pessoaModel.getStack();

        if (stack != null) {
            stack = connection.createArrayOf("varchar", pessoaModel.getStack().toArray());
        }

        var rows = jdbcTemplate.update(
                QUERY_INSERT_PESSOA,
                pessoaModel.getId(),
                pessoaModel.getNome(),
                pessoaModel.getApelido(),
                pessoaModel.getNascimento(),
                stack);

        if (rows != 1) {
            throw new RuntimeException("Não foi possível inserir o usuário");
        }
    }

    public PessoaModel findById(String id) {
        var result = jdbcTemplate.query(
                QUERY_FIND_PESSOA,
                (rs, i) -> {
                    var array = rs.getArray("stack");
                    return new PessoaModel(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("name"),
                            rs.getString("nick"),
                            rs.getString("birth_date"),
                            array == null ? null : List.of((String[]) array.getArray()));
                },
                id);

        if (result.isEmpty()) {
            throw new NotFoundHttpException();
        }

        return result.get(0);
    }

    public Integer count() {
        return jdbcTemplate.queryForObject(
                QUERY_COUNT,
                Integer.class);
    }

    public List<PessoaModel> findByTermo(String termo) {
        return jdbcTemplate.query(
                """
                        SELECT * FROM tb_people
                        WHERE plainto_tsquery('people', ?) @@ search
                        LIMIT 50
                        """,
                (rs, i) -> new PessoaModel(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("name"),
                        rs.getString("nick"),
                        rs.getString("birth_date"),
                        List.of((String[]) rs.getArray("stack").getArray())),
                termo
        );
    }
}
