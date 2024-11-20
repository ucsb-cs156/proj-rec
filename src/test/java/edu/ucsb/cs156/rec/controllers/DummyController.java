package edu.ucsb.cs156.rec.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ucsb.cs156.rec.errors.EntityNotFoundException;

/** The sole purpose of this class is only to provide tests for the
  *   ApiController and the EntityNotFoundException classes
  **/

@RequestMapping("/dummycontroller")
@RestController
public class DummyController extends ApiController {

    /**
     * This method allows us to test the EntityNotFoundException and the
     *  error handler in ApiController
     * @param id a dummy id
     * @return "String1" if id is 1, otherwise throw an exception
     */
    @GetMapping("")
    public String getById(@RequestParam Long id) throws EntityNotFoundException {
        if (id == 1) {
            return "String1";
        }
        throw new EntityNotFoundException(String.class, id);
    }
}
