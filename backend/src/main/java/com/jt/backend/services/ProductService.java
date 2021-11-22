package com.jt.backend.services;

import com.jt.backend.dto.ProductDTO;
import com.jt.backend.entities.Product;
import com.jt.backend.repositories.ProductRepository;
import com.jt.backend.services.exceptions.DatabaseException;
import com.jt.backend.services.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;


@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    //Cria findAll para buscar todas as categorias paginadas
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
        Page<Product> list = repository.findAll(pageRequest);

        // converte Ctegory para CategoryDTo
        return list.map(ProductDTO::new);

    }

    //Cria finbyid para buscar categoria por id
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> obj = repository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entidade nao esta funcionando"));
        return new ProductDTO(entity, entity.getCategories());
    }

    //Cria insert para inserir nova categorias
    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product();
        //entity.setName(dto.getName());
        entity = repository.save(entity);
        return new ProductDTO(entity);

    }

    //Cria update de categoria
    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = repository.getById(id);
            //entity.setName(dto.getName());
            entity = repository.save(entity);
            return new ProductDTO(entity);
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
