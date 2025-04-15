package com.example.demo.temp.repository;

import com.example.demo.temp.model.TempData;
import org.springframework.data.repository.CrudRepository;

public interface TempDataRepository extends CrudRepository<TempData, Long> {
}