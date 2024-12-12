package main.java.ua.nure.cpp.jdbc.dao.abstr.daos;

import main.java.ua.nure.cpp.jdbc.dao.abstr.ConnectionManager;
import main.java.ua.nure.cpp.jdbc.dao.abstr.DBException;
import main.java.ua.nure.cpp.jdbc.dao.abstr.OperationWasNotPerformedException;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface Processable<T> {
    T map(ResultSet rs) throws SQLException;

    default @NotNull Optional<Object> getColumnValue(
            @NotNull ConnectionManager cm,
            @NotNull String query,
            @NotNull String columnName,
            Object... params) {
        try (Connection con = cm.getConnection()){
             return getColumnValue(con, query, columnName, params);
        } catch (SQLException e) {
            throw new DBException("The " + query + " with params: " + Arrays.toString(params) + " was not executed correctly", e);
        }
    }

    default <E> Optional<E> getColumnValue(
            @NotNull Connection con,
            @NotNull String query,
            @NotNull String columnName,
            Object... params) {
        try (PreparedStatement ps = con.prepareStatement(query)) {

            setParamsToPreparedStatement(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Retrieve the column value and cast to the expected type
                    E val = (E)rs.getObject(columnName);
                    return Optional.ofNullable(val);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DBException("The " + query + " with params: " + Arrays.toString(params) + " was not executed correctly", e);
        }
    }

    default Optional<T> getSingle(
            @NotNull List<T> ls){
        return ls.isEmpty() ? Optional.empty() : Optional.of(ls.getFirst());
    }

    private @NotNull List<T> processSet(
            @NotNull ResultSet rs) throws SQLException {
        List<T> ls = new ArrayList<>();
        while(rs.next())
            ls.add(map(rs));

        return ls;
    }

    default @NotNull List<Object> getGeneratedKeysFromSet(
            @NotNull ResultSet rs){
        try {
            List<Object> ls = new ArrayList<>();
            for (int k = 1; rs.next(); ++k) {
                ls.add(rs.getObject(k));
            }
            return ls;
        }catch(SQLException ex){
            throw new DBException(ex);
        }
    }

    default @NotNull List<T> processQuery(
            ConnectionManager cm,
            String query,
            Object @NotNull ... params){
        try (Connection con = cm.getConnection()){
            return processQuery(con, query, params);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    default @NotNull List<T> processQuery(
            Connection con,
            String query,
            Object @NotNull ... params){
        try (PreparedStatement ps = con.prepareStatement(query)){

            for(int i = 0; i < params.length; ++i)
                ps.setObject(i + 1, params[i]);

            try(ResultSet rs = ps.executeQuery()) {
                return processSet(rs);
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    default void processUpdate(
            ConnectionManager cm,
            String query,
            Object @NotNull ... params){
        try(Connection con = cm.getConnection();
            PreparedStatement ps = con.prepareStatement(query)){

            ConnectionManager.beginTransaction(con);

            setParamsToPreparedStatement(ps, params);
            if(ps.executeUpdate() == 0){
                throw new OperationWasNotPerformedException("Cannot execute " + ps);
            }
            ConnectionManager.endTransaction(con);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    default void processUpdate(
            Connection con,
            String query,
            Object @NotNull ... params){
        try{
            ConnectionManager.requireValidConnection(con);

            try(PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                setParamsToPreparedStatement(ps, params);
                if(ps.executeUpdate() == 0)
                    throw new OperationWasNotPerformedException("Cannot execute " + ps);
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    default List<Object> processUpdateAndGetGeneratedKeys(
            Connection con,
            String query,
            Object... params){
        try (PreparedStatement ps = con.prepareStatement(
                query, Statement.RETURN_GENERATED_KEYS)) {
            setParamsToPreparedStatement(ps, params);

            if(ps.executeUpdate() == 0)
                throw new DBException("Error occurred when trying to execute " + ps);

            ResultSet rs = ps.getGeneratedKeys();
            return getGeneratedKeysFromSet(rs);
        } catch (SQLException ex) {
            throw new DBException(ex);
        }
    }

    default void setParamsToPreparedStatement(
            PreparedStatement st,
            Object @NotNull [] params) throws SQLException {
        for (int i = 0; i < params.length; ++i)
            st.setObject(i + 1, params[i]);
    }
}
