package com.techlabs.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.techlabs.entity.Student;

public interface StudentService {

	List<Student> findAll();

	Student findById(Long id);

	Student save(Student student);

	List<Student> saveAll(List<Student> studentList);

//	Student updateStudent(Student student);

	void deleteById(Long id);

//	void deleteAll();

//	Page<Student> getStudentPagination(int pageNumber, int pageSize);
//
//	Page<Student> getStudentPaginationInSort(int pageNumber, int pageSize, String sortProperty);
}