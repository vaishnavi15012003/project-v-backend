package com.techlabs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techlabs.entity.Student;
@Repository
public interface StudentRepository extends JpaRepository<Student, Long>{

}