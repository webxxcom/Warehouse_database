package main.java.ua.nure.cpp.jdbc.dao.mysql.daos;

import main.java.ua.nure.cpp.jdbc.entity.Journal;
import main.java.ua.nure.cpp.jdbc.dao.abstr.ConnectionManager;
import main.java.ua.nure.cpp.jdbc.dao.abstr.daos.SalesJournalDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class MySqlSalesJournalDAO implements SalesJournalDAO {
    private final ConnectionManager cm;

    public MySqlSalesJournalDAO(ConnectionManager cm) {
        this.cm = cm;
    }

    @Override
    public Optional<Journal> getById(long id) {
        return Optional.empty();
    }

    @Override
    public List<Journal> getByName(String name) {
        return List.of();
    }

    @Override
    public List<Journal> getByNameLike(String pattern) {
        return List.of();
    }

    @Override
    public List<Journal> getAll() {
        return List.of();
    }

    @Override
    public void insertAll(Journal[] entities) {
        throw new RuntimeException();
    }

    @Override
    public void insert(Journal entity) {
        throw new RuntimeException();
    }

    @Override
    public void delete(long id) {
        throw new RuntimeException();
    }

    @Override
    public Journal map(ResultSet rs) throws SQLException {
        throw new RuntimeException();
    }
}
