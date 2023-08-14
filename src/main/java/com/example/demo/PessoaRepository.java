package com.example.demo;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final NamedParameterJdbcTemplate parameterJdbcTemplate;
    private final Gson gson;

    private static final String QUERY_INSERT_PESSOA = """
            insert into tb_people (id, name, nick, birth_date, stack)
            values (:id, :name, :nick, :birth_date, :stack);
            """;

    @Autowired
    public PessoaRepository(DataSource dataSource, NamedParameterJdbcTemplate parameterJdbcTemplate) throws SQLException {
        this.connection = dataSource.getConnection();
        this.parameterJdbcTemplate = parameterJdbcTemplate;
    }

    public void inserePessoa(PessoaModel pessoaModel) throws SQLException {
        var stack = pessoaModel.getStack() == null
                ? null
                : connection.createArrayOf("varchar", pessoaModel.getStack().toArray());

        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("id", pessoaModel.getId())
                .addValue("name", pessoaModel.getName())
                .addValue("nick", pessoaModel.getNick())
                .addValue("birth_date", pessoaModel.getBirthDate())
                .addValue("stack", stack);

        parameterJdbcTemplate.update(
                QUERY_INSERT_PESSOA,
                parameterSource);
    }

    public PessoaModel findById(String id) {
        return parameterJdbcTemplate.queryForObject(
                "SELECT * FROM tb_people WHERE id = :id",
                Map.of("id", id),
                (rs, i) -> new PessoaModel(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("name"),
                        rs.getString("nick"),
                        rs.getTimestamp("birth_date").toLocalDateTime().toLocalDate(),
                        (List<String>) rs.getArray("stack"))
        );
    }
}
