package lt.dualpair.server.interfaces.dto.assembler;

import lt.dualpair.server.domain.model.match.SearchParameters;
import lt.dualpair.server.interfaces.dto.SearchParametersDTO;
import org.springframework.stereotype.Component;

@Component
public class SearchParametersDTOAssembler extends DTOAssembler<SearchParameters, SearchParametersDTO> {

    @Override
    public SearchParameters toEntity(SearchParametersDTO dto) {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setSearchMale(dto.getSearchMale());
        searchParameters.setSearchFemale(dto.getSearchFemale());
        searchParameters.setMinAge(dto.getMinAge());
        searchParameters.setMaxAge(dto.getMaxAge());
        return searchParameters;
    }

    @Override
    public SearchParametersDTO toDTO(SearchParameters searchParameters) {
        SearchParametersDTO dto = new SearchParametersDTO();
        dto.setSearchMale(searchParameters.getSearchMale());
        dto.setSearchFemale(searchParameters.getSearchFemale());
        dto.setMinAge(searchParameters.getMinAge());
        dto.setMaxAge(searchParameters.getMaxAge());
        return dto;
    }
}
