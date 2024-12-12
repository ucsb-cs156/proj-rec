package edu.ucsb.cs156.rec.repositories;

import edu.ucsb.cs156.rec.entities.RequestType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The RequestTypeRepository is a repository for RequestType entities.
 */
@Repository
public interface RequestTypeRepository extends CrudRepository<RequestType, Long> {
  /**
   * This method returns a RequestType entity with a given requestType string.
   * @param requestType the name of the request type
   * @return Optional of RequestType (empty if not found)
   */
  Optional<RequestType> findByRequestType(String requestType);
}