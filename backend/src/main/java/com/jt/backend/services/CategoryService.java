package com.jt.backend.services;

import com.jt.backend.dto.CategoryDTO;
import com.jt.backend.entities.Category;
import com.jt.backend.repositories.CategoryRepository;
import com.jt.backend.services.exceptions.DatabaseException;
import com.jt.backend.services.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;


@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    //Cria findAll para buscar todas as categorias paginadas
    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(Pageable pageable) {
        Page<Category> list = repository.findAll(pageable);

        // converte Ctegory para CategoryDTo
        return list.map(CategoryDTO::new);

    }

    //Cria finbyid para buscar categoria por id
    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Optional<Category> obj = repository.findById(id);
        Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entidade nao esta funcionando"));
        return new CategoryDTO(entity);
    }

    //Cria insert para inserir nova categorias
    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category entity = new Category();
        entity.setName(dto.getName());
        entity = repository.save(entity);
        return new CategoryDTO(entity);

    }

    //Cria update de categoria
    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        try {
            Category entity = repository.getById(id);
            entity.setName(dto.getName());
            entity = repository.save(entity);
            return new CategoryDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    //Cria delete de categoria
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found  + id");
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");

        }

    }
}
