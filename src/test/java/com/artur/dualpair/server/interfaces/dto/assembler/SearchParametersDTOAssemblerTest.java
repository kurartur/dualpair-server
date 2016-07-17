package com.artur.dualpair.server.interfaces.dto.assembler;

import com.artur.dualpair.server.domain.model.match.SearchParameters;
import com.artur.dualpair.server.interfaces.dto.SearchParametersDTO;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SearchParametersDTOAssemblerTest {

    private SearchParametersDTOAssembler assembler = new SearchParametersDTOAssembler();

    @Test
    public void testToDTO() throws Exception {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setMinAge(20);
        searchParameters.setMaxAge(30);
        searchParameters.setSearchFemale(true);
        searchParameters.setSearchMale(true);
        SearchParametersDTO dto = assembler.toDTO(searchParameters);
        assertEquals((Integer)20, dto.getMinAge());
        assertEquals((Integer)30, dto.getMaxAge());
        assertTrue(dto.getSearchFemale());
        assertTrue(dto.getSearchMale());
    }

    @Test
    public void testToEntity() throws Exception {

    }
}