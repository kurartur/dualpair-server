package com.artur.dualpair.server.interfaces.dto.assembler;

import java.util.HashSet;
import java.util.Set;

public abstract class DTOAssembler<E, D> {

    public abstract E toEntity(D d);
    public abstract D toDTO(E e);

    public Set<E> toEntitySet(Set<D> objectSet) {
        Set<E> entitySet = new HashSet<>();
        for(D object : objectSet) {
            entitySet.add(toEntity(object));
        }
        return entitySet;
    }

    public Set<D> toDTOSet(Set<E> entitySet) {
        Set<D> objectSet = new HashSet<>();
        for(E entity : entitySet) {
            objectSet.add(toDTO(entity));
        }
        return objectSet;
    }

}
