package com.example.demo.repository;

import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.entity.Category;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    boolean existsByName(String name);
    Optional<Category> findById(Long id);
//    List<Category> findByParentIsNull();
    void deleteById(Long id);
}
