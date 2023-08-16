package com.example.demo;

import com.example.demo.exceptions.NotFoundHttpException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PessoaRepository {
    private final Connection connection;
    private final JdbcTemplate jdbcTemplate;

    private static final String QUERY_INSERT_PESSOA = """
            insert into tb_people (id, name, nick, birth_date, stack)
            values (?, ?, ?, ?, ?)
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
        var stack = pessoaModel.getStack() == null
                ? null
                : connection.createArrayOf("varchar", pessoaModel.getStack().toArray());

        jdbcTemplate.update(
                QUERY_INSERT_PESSOA,
                pessoaModel.getId(),
                pessoaModel.getName(),
                pessoaModel.getNick(),
                pessoaModel.getBirthDate(),
                stack);
    }

    public PessoaModel findById(String id) {
        var result = jdbcTemplate.query(
                QUERY_FIND_PESSOA,
                (rs, i) -> new PessoaModel(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("name"),
                        rs.getString("nick"),
                        rs.getTimestamp("birth_date").toLocalDateTime().toLocalDate(),
                        List.of((String[]) rs.getArray("stack").getArray())),
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
                WHERE to_tsquery('people', ?) @@ search
                LIMIT 50
                """,
                (rs, i) -> new PessoaModel(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("name"),
                        rs.getString("nick"),
                        rs.getTimestamp("birth_date").toLocalDateTime().toLocalDate(),
                        List.of((String[]) rs.getArray("stack").getArray())),
                termo
        );
    }
}
