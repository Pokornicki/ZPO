package com.project.service;

import com.project.model.Student;
import com.project.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Optional<Student> getStudent(Integer studentId) {
        return studentRepository.findById(studentId);
    }

    @Override
    public Optional<Student> getStudentByNrIndeksu(String nrIndeksu) {
        return studentRepository.findByNrIndeksu(nrIndeksu);
    }

    @Override
    public Student setStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public void deleteStudent(Integer studentId) {
        studentRepository.deleteById(studentId);
    }

    @Override
    public Page<Student> getStudenci(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    @Override
    public Page<Student> searchByNrIndeksu(String nrIndeksu, Pageable pageable) {
        return studentRepository.findByNrIndeksuStartsWith(nrIndeksu, pageable);
    }

    @Override
    public Page<Student> searchByNazwisko(String nazwisko, Pageable pageable) {
        return studentRepository.findByNazwiskoStartsWithIgnoreCase(nazwisko, pageable);
    }
}