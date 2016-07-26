package com.artur.dualpair.server.infrastructure.persistence.repository;

import com.artur.dualpair.server.domain.model.socionics.test.Choice;
import org.springframework.data.jpa.repository.Query;

public interface ChoiceRepository extends ReadOnlyRepository<Choice, Integer> {

    @Query("from Choice c where c.code = ?1")
    Choice findByCode(String code);

}
