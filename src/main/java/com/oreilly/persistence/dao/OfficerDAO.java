package com.oreilly.persistence.dao;

import com.oreilly.persistence.entities.Officer;

import java.util.List;
import java.util.Optional;

public interface OfficerDAO {

   Officer save(Officer officer);

   Optional<Officer> findById(Integer id);

   List<Officer> findAll();

   void delete(Officer officer);

   long count();

   boolean existsById(Integer id);
}
