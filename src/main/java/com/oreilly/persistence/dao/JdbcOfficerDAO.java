package com.oreilly.persistence.dao;

import com.oreilly.persistence.entities.Officer;
import com.oreilly.persistence.entities.Rank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcOfficerDAO implements OfficerDAO {

   private final JdbcTemplate jdbcTemplate;
   private final SimpleJdbcInsert insertOfficer;

   @Autowired
   public JdbcOfficerDAO(JdbcTemplate jdbcTemplate) {

      this.jdbcTemplate = jdbcTemplate;
      this.insertOfficer = new SimpleJdbcInsert(jdbcTemplate).withTableName("officers").usingGeneratedKeyColumns("id");
   }

   @Override
   public Officer save(Officer officer) {

      Map<String, Object> parameters = new HashMap<>();
      parameters.put("rank", officer.getRank());
      parameters.put("first_name", officer.getFirstName());
      parameters.put("last_name", officer.getLastName());
      Integer newId = (Integer) insertOfficer.executeAndReturnKey(parameters);
      officer.setId(newId);
      return officer;
   }

   @Override
   public Optional<Officer> findById(Integer id) {

      if(!existsById(id))  return Optional.empty();

      return Optional.of(
            jdbcTemplate.queryForObject(
                  "select * from officers where id = ?",
                  new RowMapper<Officer>() {
                     @Override
                     public Officer mapRow(ResultSet rs, int rowNum) throws SQLException {

                        return new Officer(
                              rs.getInt("id"),
                              Rank.valueOf(rs.getString("rank")),
                              rs.getString("first_name"),
                              rs.getString("last_name")
                        );
                     }
                  },
                  id)
      );
   }

   @Override
   public List<Officer> findAll() {

      return jdbcTemplate.query(
            "select * from officers",
            (rs, rowNum) -> new Officer(
                  rs.getInt("id"),
                  Rank.valueOf(rs.getString("rank")),
                  rs.getString("first_name"),
                  rs.getString("last_name")
            )
      );
   }

   @Override
   public void delete(Officer officer) {

      jdbcTemplate.update("delete from officers where id = ?", officer.getId());
   }

   @Override
   public long count() {

      return jdbcTemplate.queryForObject("select count(1) from officers", Long.class);
   }

   @Override
   public boolean existsById(Integer id) {

      return jdbcTemplate.queryForObject("select EXISTS(select 1 from officers where id = ?)", Boolean.class, id);
   }
}
