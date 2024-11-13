package edu.ucsb.cs156.rec.repositories;

import edu.ucsb.cs156.rec.entities.UCSBDiningCommons;

import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * The UCSBDiningCommonsRepository is a repository for UCSBDiningCommons entities
 */
@Repository
public interface UCSBDiningCommonsRepository extends CrudRepository<UCSBDiningCommons, String> {
 
}